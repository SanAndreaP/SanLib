(function() {
	let import_model;
	let import_parent;
	let export_model;

	Plugin.register('sanlib_entity_models_blockbench', {
		title: 'SanLib Entity Model Import & Export',
		author: 'SanAndreaP',
		icon: 'domain',
		description: 'Import and export entity models used by SanLib',
		version: '0.3.0',
		variant: 'both',

		onload() {
			import_model = new Action({
				id: "sanlib_entity_imp",
				name: "Open SanLib Entity Model",
				icon: "brightness_7",
				category: "file",
				click: importFile
			});
			import_parent = new Action({
				id: "sanlib_entity_pnt",
				name: "Import SanLib Entity Model (As Parent)",
				icon: "brightness_5",
				category: "file",
				click: importParent
			});
			export_model = new Action({
				id: "sanlib_entity_exp",
				name: "Save SanLib Entity Model",
				icon: "brightness_7",
				category: "file",
				click: saveFile
			});

			MenuBar.addAction(import_model, 'file');
			MenuBar.addAction(import_parent, 'file.import');
			MenuBar.addAction(export_model, 'file.export');
		},

		onunload() {
			import_model.delete();
			import_parent.delete();
			export_model.delete();
		}
	});

	function importFile() {
		Blockbench.import({
			extensions: ["json"],
			type: "SanLib Entity Model"
		}, function(files) {
			loadFile(files[0].content, files[0].path);
		})
	}

	function importParent() {
		Blockbench.import({
			extensions: ["json"],
			type: "SanLib Entity Model"
		}, function(files) {
			loadFile(files[0].content, files[0].path, null, true);

			Project.parent = getResourceLocation(files[0].path);
		})
	}

	function importParentFile(content, path, data) {
		Blockbench.import({
			extensions: ["json"],
			type: "SanLib Parent Entity Model"
		}, function(files) {
			let newData = data;
			let newCubes = {};
			while( newData.parent ) {
				newData = JSON.parse(files[0].content);
				newCubes = joinCubeList(newData.cubes, newCubes);
			}
			loadFile(content, path, Object.keys(newCubes).map(function(key) { return newCubes[key].cube; }));

			Project.parent = data.parent;
		})
	}

	function loadFile(content, path, parentCubes, asParent) {
		if( !parentCubes && !asParent ) {
			newProject(Formats.modded_entity);
		}

		const data = JSON.parse(content);
		let cubes = joinCubeList(parentCubes || [], joinCubeList(data.cubes));

		if( !parentCubes ) {
			if( data.parent ) {
				if( Blockbench.isMobile || Blockbench.isWeb || !path ) {
					importParentFile(content, path, data)
					return;
				} else {
					let newData = data;
					while( newData.parent ) {
						newData = JSON.parse(fs.readFileSync(getAssetPath(path, newData.parent)));
						cubes = joinCubeList(newData.cubes, cubes);
					}

					Project.parent = data.parent;
				}
			} else {
				Project.parent = null;
			}
		}

		if( !parentCubes && !asParent ) {
			Project.name = data.name || extractFileName(path || "unknown_model");
		}

		var cubeNames = Object.keys(cubes);

		if( cubeNames.length < 2 ) {
			Blockbench.showMessage("Cannot load model: It has no cubes defined!", "status_bar");
			console.warn('sanlib-entity-models.loadFile: no cubes defined.')
			return;
		}

		if( !parentCubes ) {
			Project.texture_width = cubes.__firstCube.textureWidth;
			Project.texture_height = cubes.__firstCube.textureHeight;
		}

		Project.textures.splice(0, Project.textures.length);
		if( data.texture && !(Blockbench.isMobile || Blockbench.isWeb) ) {
			var texture = new Texture().fromPath(getAssetPath(path, data.texture));
			if( texture && !texture.error && fs.existsSync(texture.path) ) {
				Project.textures.push(texture);
			}
		}

		const groups = {};
		let cubeData;

		const buildGroup = function(n) {
			if( groups[n] ) {
				return groups[n];
			}

			let parent = cubes[n].cube.parentBox;
			if( parent ) {
				parent = buildGroup(parent);
			}

			let group = new Group({
				name: n
			}).addTo(parent || undefined).init();
			groups[n] = group;

			return group;
		}

		// __b = blockbench value
		//
		// rx/y/z = rotPtX/Y/Z (origin/pivot point)
		// ox/y/z = offsetX/Y/Z (position)
		// sx/y/z = sizeX/Y/Z
		//
		// rxb = -rx,                          ryb = 24 - ry,           rzb = rz;
		// oxb = -rx - ox - sx (+ rxb parent), oyb = 24 - ry - oy - sy, ozb = rz + oz;
		// sxb = sx,                           syb = sy,                szb = sz;
		for( let cubeName of cubeNames ) {
			cubeData = cubes[cubeName].cube;
			if( !cubeData ) continue;

			const parent = cubeData.parentBox ? cubes[cubeData.parentBox].cube : null;

			cubeData.offsetX = cubeData.offsetX || 0;
			cubeData.offsetY = cubeData.offsetY || 0;
			cubeData.offsetZ = cubeData.offsetZ || 0;
			cubeData.rotationPointX = cubeData.rotationPointX || 0;
			cubeData.rotationPointY = cubeData.rotationPointY || 0;
			cubeData.rotationPointZ = cubeData.rotationPointZ || 0;

			const group = buildGroup(cubeData.boxName);
			group.rotation = [-(cubeData.rotateAngleX || 0) * 180.0 / Math.PI,
				               (cubeData.rotateAngleY || 0) * 180.0 / Math.PI,
				               (cubeData.rotateAngleZ || 0) * 180.0 / Math.PI];
			group.origin = [-cubeData.rotationPointX,
				            24 - cubeData.rotationPointY,
				            cubeData.rotationPointZ];

			cubeData.offsetX = group.origin[0] - cubeData.offsetX - cubeData.sizeX - (parent ? (parent.rotationPointX || 0) : 0);
			cubeData.offsetY = group.origin[1] - cubeData.offsetY - cubeData.sizeY;
			cubeData.offsetZ = cubeData.rotationPointZ + cubeData.offsetZ;
			new Cube({
				mirror_uv: cubeData.mirror,
				name: (cubes[cubeName].isParent || asParent ? "[parent] " : "") + cubeData.boxName,
				from: [cubeData.offsetX, cubeData.offsetY, cubeData.offsetZ],
				to: [cubeData.offsetX + cubeData.sizeX, cubeData.offsetY + cubeData.sizeY, cubeData.offsetZ + cubeData.sizeZ],
				uv_offset: [cubeData.textureX || 0, cubeData.textureY || 0],
				visibility: !cubeData.isHidden
			}).addTo(group).init();
		}

		Canvas.updateAll();
	}

	function saveFile() {
		let data = {}

		if( (typeof Project.parent === 'string' || Project.parent instanceof String) && Project.parent ) {
			data["parent"] = Project.parent.toString();
		}

		if( textures.length > 0 ) {
			const rl = getResourceLocation(textures[0].path);
			if( rl ) {
				data["texture"] = rl;
			}
		}

		data["cubes"] = [];

		for( let olCube of Outliner.elements ) {
			if( olCube instanceof Cube && !olCube.name.match(/^\[parent]/i) && olCube.parent instanceof Group ) {
				let parentGroup = null;

				if( olCube.parent.name !== olCube.name ) {
					parentGroup = olCube.parent
				} else if( olCube.parent.parent instanceof Group && olCube.parent.parent.name !== "root" ) {
					parentGroup = olCube.parent.parent
				}

				const cubeData = {};

				cubeData["boxName"] = olCube.name;

				cubeData["sizeX"] = 0;
				cubeData["sizeY"] = 0;
				cubeData["sizeZ"] = 0;

				cubeData["textureX"] = olCube.uv_offset[0];
				cubeData["textureY"] = olCube.uv_offset[1];
				cubeData["textureWidth"] = Project.texture_width;
				cubeData["textureHeight"] = Project.texture_height;

				if( olCube.mirror_uv ) cubeData["mirror"] = true;
				if( !olCube.visibility ) cubeData["isHidden"] = true;

				putN0(cubeData, "rotateAngleX", -olCube.parent.rotation[0] * Math.PI / 180.0);
				putN0(cubeData, "rotateAngleY",  olCube.parent.rotation[1] * Math.PI / 180.0);
				putN0(cubeData, "rotateAngleZ",  olCube.parent.rotation[2] * Math.PI / 180.0);

				putN0(cubeData, "rotationPointX",     -olCube.parent.origin[0]);
				putN0(cubeData, "rotationPointY", 24 - olCube.parent.origin[1]);
				putN0(cubeData, "rotationPointZ",      olCube.parent.origin[2]);

				cubeData["sizeX"] = olCube.to[0] - olCube.from[0];
				cubeData["sizeY"] = olCube.to[1] - olCube.from[1];
				cubeData["sizeZ"] = olCube.to[2] - olCube.from[2];

				cubeData["offsetX"] = - olCube.from[0] - cubeData["sizeX"] - (cubeData["rotationPointX"] || 0) + (parentGroup ? parentGroup.origin[0] : 0)
				cubeData["offsetY"] = - olCube.from[1] - cubeData["sizeY"] - (cubeData["rotationPointY"] || 0) + 24;
				cubeData["offsetZ"] =   olCube.from[2] - (cubeData["rotationPointZ"] || 0);

				if( parentGroup ) cubeData["parentBox"] = parentGroup.name;

				data["cubes"].push(cubeData);
			}
		}

		Blockbench.export({
			type: 'SanLib Entity Model',
			extensions: ['json'],
			name: Project.name !== '' ? Project.name : 'entity_model',
			content: JSON.stringify(data, null, 2),
			savetype: 'json'
		});
	}

	function joinCubeList(currCubes, mainCubes) {
		if( !currCubes ) {
			return {};
		}

		let cube;
		if( !mainCubes ) {
			mainCubes = {__firstCube: null};
			for( cube of currCubes ) {
				mainCubes[cube.boxName] = {cube: cube, isParent: false};
				if( mainCubes.__firstCube === null ) {
					mainCubes.__firstCube = cube;
				}
			}
		} else {
			for( cube of currCubes ) {
				if( !(cube.boxName in mainCubes) ) {
					mainCubes[cube.boxName] = {cube: cube, isParent: true};
				}
			}
		}

		return mainCubes;
	}

	function extractFileName(path) {
		return path.split('\\').pop().split('/').pop();
	}

	function getAssetPath(path, resourceLocation) {
		const p = path.split('\\').join('/');
		return p.substr(0, p.lastIndexOf('assets/') + 7) + resourceLocation.replace(':', '/');
	}

	function getResourceLocation(path) {
		const p = path.split('\\').join('/');
		const assetPos = p.lastIndexOf('assets/');
		return assetPos >= 0 ? p.substr(assetPos + 7).replace(/\//,':') : null;
	}

	function is0(f) {
		return f >= -1e-16 && f <= 1e-16;
	}

	function putN0(data, key, val) {
		val = parseFloat(val.toFixed(16));
		if( !is0(val) ) {
			data[key] = val;
		}
	}
})();