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
import sc2toolkit.replay.model.ISubgroup;

/**
 * Data structure describing the selection delta.
 *
 * @author Andras Belicza
 */
public class Subgroup extends StructView implements ISubgroup {

  /**
   * Creates a new {@link Subgroup}.
   *
   * @param struct data structure
   */
  public Subgroup(final Map< String, Object> struct) {
    super(struct);

  }

  @Override
  public Integer getUnitLink() {
    return get(F_UNIT_LINK);
  }

  @Override
  public Integer getSubgroupPriority() {
    return get(F_SUBGROUP_PRIORITY);
  }

  @Override
  public Integer getIntraSubgroupPriority() {
    return get(F_INTRA_SUBGROUP_PRIORITY);
  }

  @Override
  public Integer getCount() {
    return get(F_COUNT);
  }

}
