/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.balancedata;

/**
 * SC2 Balance data utilities.
 *
 * @author Andras Belicza
 */
public class BdUtil {

  /**
   * Singleton instance.
   */
  public static final BdUtil INSTANCE = new BdUtil();

  /**
   * Id of the Drone unit.
   */
  private static final String UNIT_DRONE = "Drone";

  /**
   * Id of the Probe unit.
   */
  private static final String UNIT_PROBE = "Probe";

  /**
   * Id of the SCV unit.
   */
  private static final String UNIT_SCV = "SCV";

  /**
   * Id of the Broodling unit.
   */
  private static final String UNIT_BROODLING = "Broodling";

  /**
   * Id of the Broodling Escort unit.
   */
  private static final String UNIT_BROODLING_ESCORT = "BroodlingEscort";

  /**
   * Id of the Larva unit.
   */
  private static final String UNIT_LARVA = "Larva";

  /**
   * Id of the Locusts unit.
   */
  private static final String UNIT_LOCUST = "LocustMP";

  /**
   * Id of the Reaper (Placeholder) unit.
   */
  private static final String UNIT_REAPER_PLACEHOLDER = "ReaperPlaceholder";

  /**
   * Id of the Nexus unit (building).
   */
  private static final String UNIT_NEXUS = "Nexus";

  /**
   * Id of the Hatchery unit (building).
   */
  private static final String UNIT_HATCHERY = "Hatchery";

  /**
   * Id of the Command Center unit (building).
   */
  private static final String UNIT_COMMAND_CENTER = "CommandCenter";

  /**
   * Id of the Mineral Field unit.
   */
  private static final String UNIT_MINERAL_FIELD = "MineralField";

  /**
   * Id of the Mineral Field (750 minerals only) unit.
   */
  private static final String UNIT_MINERAL_FIELD_750 = "MineralField750";

  /**
   * Id of the Lab Mineral Field unit.
   */
  private static final String UNIT_LAB_MINERAL_FIELD = "LabMineralField";

  /**
   * Id of the Lab Mineral Field (750 minerals only) unit.
   */
  private static final String UNIT_LAB_MINERAL_FIELD_750 = "LabMineralField750";

  /**
   * Id of the Rich Mineral Field unit.
   */
  private static final String UNIT_RICH_MINERAL_FIELD = "RichMineralField";

  /**
   * Id of the Rich Mineral Field (750 minerals only) unit.
   */
  private static final String UNIT_RICH_MINERAL_FIELD_750 = "RichMineralField750";

  /**
   * Id of the Vespene Geyser unit.
   */
  private static final String UNIT_VESPENE_GEYSER = "VespeneGeyser";

  /**
   * Id of the Protoss Vespene Geyser unit.
   */
  private static final String UNIT_PROTOSS_VESPENE_GEYSER = "ProtossVespeneGeyser";

  /**
   * Id of the Rich Vespene Geyser unit.
   */
  private static final String UNIT_RICH_VESPENE_GEYSER = "RichVespeneGeyser";

  /**
   * Id of the Space Platform Vespene Geyser unit.
   */
  private static final String UNIT_SPACE_PLATFORM_VESPENE_GEYSER = "SpacePlatformGeyser";

  /**
   * Id of the Xel'Naga Tower unit.
   */
  private static final String UNIT_XEL_NAGA_TOWER = "XelNagaTower";

  /**
   * Creates a new {@link BdUtil}.
   * <p>
   * Private to enforce singleton nature.
   * </p>
   */
  private BdUtil() {
  }

  /**
   * Tells if the specified unit id is an id of a worker unit.
   *
   * @param id unit id to be tested
   * @return true if the specified unit id is an id of a worker unit; false
   *         otherwise
   */
  public static boolean isWorkerImpl(final String id) {
    return UNIT_DRONE.equals(id) || UNIT_PROBE.equals(id) || UNIT_SCV.equals(id);
  }

  /**
   * Tells if the specified unit (building) id is an id of a main building unit.
   *
   * @param id unit (building) id to be tested
   * @return true if the specified unit id is an id of a main building unit;
   *         false otherwise
   */
  public static boolean isMainBuildingImpl(final String id) {
    return UNIT_NEXUS.equals(id) || UNIT_HATCHERY.equals(id) || UNIT_COMMAND_CENTER.equals(id);
  }

  /**
   * Tells if the specified unit id is an id of a destructible unit.
   *
   * @param id unit id to be tested
   * @return true if the specified unit id is an id of a destructible unit;
   *         false otherwise
   */
  public static boolean isDestructibleImpl(final String id) {
    return id.startsWith("Destructible");
  }

  public boolean isWorker(final String id) {
    return isWorkerImpl(id);
  }

  public boolean isMainBuilding(final String id) {
    return isMainBuildingImpl(id);
  }

  public boolean isDestructible(final String id) {
    return isDestructibleImpl(id);
  }

}
