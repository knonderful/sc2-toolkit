/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.impl;

import java.util.Map;
import sc2toolkit.replay.model.ICamTargetPoint;

/**
 * Data structure describing a camera target point.
 *
 * @author Andras Belicza
 */
public class CamTargetPoint extends StructView implements ICamTargetPoint {

  /**
   * Creates a new {@link TargetPoint}.
   *
   * @param struct data structure
   */
  public CamTargetPoint(final Map< String, Object> struct) {
    super(struct);
  }

  @Override
  public Integer getX() {
    return get(F_X);
  }

  @Override
  public Float getXFloat() {
    final Integer x = get(F_X);
    return x == null ? null : x / 256.0f;
  }

  @Override
  public Integer getY() {
    return get(F_Y);
  }

  @Override
  public Float getYFloat() {
    final Integer y = get(F_Y);
    return y == null ? null : y / 256.0f;
  }

}
