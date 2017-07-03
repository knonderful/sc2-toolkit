/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.model;

import java.awt.Color;

import javax.swing.Icon;

/**
 * Player color.
 *
 * @author Andras Belicza
 */
public enum PlayerColor {

  /**
   * Unknown.
   */
  UNKNOWN("Unknown", new Color(150, 150, 150)),
  /**
   * Red.
   */
  RED("Red", new Color(180, 20, 30)),
  /**
   * Blue.
   */
  BLUE("Blue", new Color(0, 66, 255)),
  /**
   * Teal.
   */
  TEAL("Teal", new Color(28, 167, 234)),
  /**
   * Purple.
   */
  PURPLE("Purple", new Color(84, 0, 129)),
  /**
   * Yellow.
   */
  YELLOW("Yellow", new Color(235, 225, 41)),
  /**
   * Orange.
   */
  ORANGE("Orange", new Color(254, 138, 14)),
  /**
   * Green.
   */
  GREEN("Green", new Color(22, 128, 0)),
  /**
   * Light pink.
   */
  LIGHT_PINK("Light Pink", new Color(204, 166, 252)),
  /**
   * Violet.
   */
  VIOLET("Violet", new Color(31, 1, 201)),
  /**
   * Light gray.
   */
  LIGHT_GRAY("Light Gray", new Color(82, 84, 148)),
  /**
   * Dark green.
   */
  DARK_GREEN("Dark Green", new Color(16, 98, 70)),
  /**
   * Brown.
   */
  BROWN("Brown", new Color(78, 42, 4)),
  /**
   * Light green.
   */
  LIGHT_GREEN("Light Green", new Color(150, 255, 145)),
  /**
   * Dark gray.
   */
  DARK_GRAY("Dark Gray", new Color(35, 35, 35)),
  /**
   * Pink.
   */
  PINK("Pink", new Color(229, 91, 176));

  /**
   * Text value of the player color.
   */
  public final String text;

  /**
   * Party color value used for {@link AttributesEvents#A_PARTY_COLOR}.
   */
  public final String partyColor;

  /**
   * Color value of this player color.
   */
  public final Color color;

  /**
   * A darker color value of this player color.
   */
  public final Color darkerColor;

  /**
   * A brighter color value of this player color.
   */
  public final Color brighterColor;

  /**
   * Creates a new {@link Color}.
   *
   * @param text  text value
   * @param color {@link Color} value of this player color
   */
  private PlayerColor(final String text, final Color color) {
    this.text = text;
    this.color = color;
    // Color.darker() is not enough:
    darkerColor = new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);
    brighterColor = new Color(127 + darkerColor.getRed(), 127 + darkerColor.getGreen(), 127 + darkerColor.getBlue());

    final int ordinalPlusOne = ordinal() + 1;
    partyColor = ordinalPlusOne < 10 ? "tc0" + ordinalPlusOne : "tc" + ordinalPlusOne;

  }

  public Color getColor() {
    return color;
  }

  public Color getDarkerColor() {
    return darkerColor;
  }

  public Color getBrighterColor() {
    return brighterColor;
  }

  @Override
  public String toString() {
    return text;
  }

  /**
   * Cache of the values array.
   */
  public static final PlayerColor[] VALUES = values();

}
