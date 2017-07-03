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

import java.util.List;

/**
 * Player selection tracker.
 *
 * @since 1.3
 *
 * @author Andras Belicza
 *
 * @see IFactory#newSelectionTracker()
 */
public interface ISelectionTracker {

  /**
   * Returns the selections (list of unit links) for the 10 control groups and
   * an extra one for the active selection.<br>
   * A selection is a list of arrays holding the link and tag of a unit.
   * Multiple units of the same type are listed multiple times properly. Order
   * is important.
   *
   * @return the selections for the 10 control groups and an extra one for the
   *         active selection
   *
   * @see #getActiveSelection()
   */
  List< Integer[]>[] getCgSelections();

  /**
   * Returns the active selection (which is the last element of the array
   * returned by {@link #getCgSelections()}).
   *
   * @return the active selection
   *
   * @see #getCgSelections()
   */
  List< Integer[]> getActiveSelection();

  /**
   * Processes the specified {@link ISelectionDeltaEvent}, updates the denoted
   * control group selection.
   *
   * @param event selection delta event to be processed
   *
   * @see #processControlGroupUpdate(IControlGroupUpdateEvent)
   */
  void processSelectionDelta(ISelectionDeltaEvent event);

  /**
   * Processes the specified {@link IControlGroupUpdateEvent}, updates the
   * denoted control group selection.
   *
   * @param event control group update event to be processed
   *
   * @see #processSelectionDelta(ISelectionDeltaEvent)
   */
  void processControlGroupUpdate(IControlGroupUpdateEvent event);

}
