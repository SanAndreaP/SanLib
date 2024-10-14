# GuiElement

### ![Description][desc]
The base type of all elements. The following types are available:

| Name         | ID             |
|--------------|----------------|
| Button       | `button`       |
| Rectangle    | `rectangle`    |
| Scroll Panel | `scroll_panel` |
| Stack Panel  | `stack_panel`  |
| Text         | `text`         |
| Textfield    | `textfield`    |
| Texture      | `texture`      |

### ![Parameters][param]
| Name          | Type               | Required      | Default | Description                                                               |
|---------------|--------------------|---------------|---------|---------------------------------------------------------------------------|
| `type`        | `ResourceLocation` | ![yes][check] |         | ID for the type of this element being loaded                              |
| `posX` [^1]   | `int`              | ![no][cross]  | `0`     | Horizontal position in pixels<br/>relative from its parent; left to right |
| `posY` [^1]   | `int`              | ![no][cross]  | `0`     | Vertical position in pixels<br/>relative from its parent; top to bottom   |
| `hAlignment`  | `Alignment`        | ![no][cross]  | `LEFT`  | horizontal alignment relative to its parent                               |
| `vAlignment`  | `Alignment`        | ![no][cross]  | `TOP`   | vertical alignment relative to its parent                                 |
| `width` [^2]  | `int`              | ![no][cross]  | `0`     | width of the element in pixels                                            |
| `height` [^2] | `int`              | ![no][cross]  | `0`     | height of the element in pixels                                           |

### ![Examples][example]
**simple 15x15 red rectangle at (5, 15)**
```json
{
  "type": "rectangle",
  "posX": 10,
  "posY": 10,
  "width": 5,
  "height": 15,
  "color": "#FFFF0000"
}
```

**custom element from another mod**
```json
{
  "type": "examplemod:bouncy_item",
  "posX": 32,
  "item": "minecraft:stick",
  "bounceTime": 12
}
```

[^1]: some elements may change position or have their position changed by their parent. In which case it considers these parameters as their starting position or offset

[^2]: some elements are resizable or auto-size themselves, ignoring these parameters

[desc]: ../img/desc.svg "Description"
[param]: ../img/parameters.svg "Parameters"
[example]: ../img/example.svg "Example"
[check]: ../img/yes.svg "yes"
[cross]: ../img/no.svg "no"
