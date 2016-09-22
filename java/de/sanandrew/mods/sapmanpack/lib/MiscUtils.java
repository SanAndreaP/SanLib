/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.lib;

/**
 * Utility class for miscellaneous tasks and methods
 */
public final class MiscUtils
{
    /** Method to calculate a value based on a formula represented as a {@link java.lang.String}.<br>
     * <i>Code from <a href="http://stackoverflow.com/a/26227947">http://stackoverflow.com/a/26227947</a> with minor changes</i>
     * @param str Formula to calculate, like {@code "2 + 2"}. Also accepts parentheses grouping,
     *            ({@code a}){@code sin}/({@code a}){@code cos}/({@code a}){@code tan}/{@code sqrt} functions and exponentials via " {@code ^} "
     * @return A calculated value from the formula, or {@code null} if the formula is invalid
     */
    public static Double calcFormula(final String str) {
        return new Object() {
            private int pos = -1, ch;

            private void nextChar() {
                this.ch = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
            }

            private boolean eat(int charToEat) {
                while( ch == ' ' ) {
                    this.nextChar();
                }

                if( ch == charToEat ) {
                    this.nextChar();
                    return true;
                }

                return false;
            }

            private Double parse() {
                this.nextChar();
                double x = parseExpression();

                if( this.pos < str.length() ) {
                    return null;
                }

                return x;
            }

            private double parseExpression() {
                double x = this.parseTerm();
                while(true) {
                    if( eat('+') ) {
                        x += parseTerm(); // addition
                    } else if( eat('-') ) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            private double parseTerm() {
                Double x = parseFactor();
                if( x == null ) {
                    return 0.0D;
                }
                while(true) {
                    if( eat('*') ) {
                        x *= parseFactor(); // multiplication
                    } else if( eat('/') ) {
                        x /= parseFactor(); // division
                    } else {
                        return x;
                    }
                }
            }

            private Double parseFactor() {
                double sign = 1.0D;
                while( true ) {
                    if( eat('+') ) {
                        continue;
                    }
                    if( eat('-') ) {
                        sign *= -1.0D;
                        continue;
                    }

                    Double x;
                    int startPos = this.pos;
                    if( eat('(') ) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if( (ch >= '0' && ch <= '9') || ch == '.' ) { // numbers
                        while( (ch >= '0' && ch <= '9') || ch == '.' ) {
                            nextChar();
                        }
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if( ch >= 'a' && ch <= 'z' ) { // functions
                        while( ch >= 'a' && ch <= 'z' ) {
                            nextChar();
                        }
                        String func = str.substring(startPos, this.pos);
                        x = parseFactor();
                        if( x == null ) {
                            return null;
                        }
                        switch( func ) {
                            case "sqrt":
                                x = Math.sqrt(x);
                                break;
                            case "sin":
                                x = StrictMath.sin(Math.toRadians(x));
                                break;
                            case "asin":
                                x = StrictMath.sin(Math.toRadians(x));
                                break;
                            case "cos":
                                x = StrictMath.cos(Math.toRadians(x));
                                break;
                            case "acos":
                                x = StrictMath.cos(Math.toRadians(x));
                                break;
                            case "tan":
                                x = StrictMath.tan(Math.toRadians(x));
                                break;
                            case "atan":
                                x = StrictMath.atan(Math.toRadians(x));
                                break;
                            default:
                                return null;
                        }
                    } else {
                        return null;
                    }

                    if( eat('^') ) {
                        Double y = parseFactor();
                        if( y == null ) {
                            return null;
                        }
                        x = StrictMath.pow(x, y); // exponentiation
                    }

                    return x * sign;
                }
            }
        }.parse();
    }
}
