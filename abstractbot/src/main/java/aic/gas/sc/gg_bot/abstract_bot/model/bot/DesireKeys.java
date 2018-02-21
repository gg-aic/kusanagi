package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration of all IDs for desires as static classes
 */
public enum DesireKeys {
  /*
   * LEARNT DESIRES
   */

  //ECO_MANAGER's desires
  EXPAND,
  BUILD_EXTRACTOR,
  BUILD_WORKER,
  INCREASE_CAPACITY,

  //BUILDING_ORDER_MANAGER's desires
  ENABLE_AIR,
  ENABLE_GROUND_RANGED,
  ENABLE_STATIC_ANTI_AIR,
  ENABLE_GROUND_MELEE,
  UPGRADE_TO_LAIR,

  //UNIT_ORDER_MANAGER's desires
  BOOST_AIR,
  BOOST_GROUND_MELEE,
  BOOST_GROUND_RANGED,

  //BASE_LOCATION's desires
  HOLD_GROUND,
  HOLD_AIR,

  //defend (base is present)
  BUILD_CREEP_COLONY,
  BUILD_SUNKEN_COLONY,
  BUILD_SPORE_COLONY,

  /*
   * HARD-CODED DESIRES
   */

  //desires of agent representing player
  READ_PLAYERS_DATA,
  ESTIMATE_ENEMY_FORCE_IN_BUILDINGS,
  ESTIMATE_ENEMY_FORCE_IN_UNITS,
  ESTIMATE_OUR_FORCE_IN_BUILDINGS,
  ESTIMATE_OUR_FORCE_IN_UNITS,
  UPDATE_ENEMY_RACE,
  REASON_ABOUT_BASES,
  ESTIMATE_ARMY_SUPPLY_RATIO,

  //desires for agent representing base
  REASON_ABOUT_BASE_TYPE,
  ECO_STATUS_IN_LOCATION,
  ESTIMATE_ENEMY_FORCE_IN_LOCATION,
  ESTIMATE_OUR_FORCE_IN_LOCATION,
  FRIENDLIES_IN_LOCATION,
  ENEMIES_IN_LOCATION,
  MINE_MINERALS_IN_BASE,
  MINE_GAS_IN_BASE,
  REASON_ABOUT_OUR_BASE,
  REASON_ABOUT_ENEMY_BASE_WEAKNESSES,
  REASON_ABOUT_DISTANCES,
  REASON_ABOUT_SUFFERING_AND_INFLICTING_DMG,
  REASON_GLOBAL_SUPPLY_VS_LOCAL_ENEMIES_RATIO,

  //desires for agent's representing unit
  SURROUNDING_UNITS_AND_LOCATION,

  //desire - morphing
  MORPHING_TO,
  MORPH_TO_DRONE,
  MORPH_TO_POOL,
  MORPH_TO_OVERLORD,
  MORPH_TO_EXTRACTOR,
  MORPH_TO_SPIRE,
  MORPH_TO_HYDRALISK_DEN,
  MORPH_TO_SPORE_COLONY,
  MORPH_TO_CREEP_COLONY,
  MORPH_TO_SUNKEN_COLONY,
  MORPH_TO_EVOLUTION_CHAMBER,

  //desires for worker
  UPDATE_BELIEFS_ABOUT_WORKER_ACTIVITIES,
  GO_TO_BASE,
  RETURN_TO_BASE,
  STOP_BUILD,
  FIND_PLACE_FOR_POOL,
  FIND_PLACE_FOR_HATCHERY,
  FIND_PLACE_FOR_EXTRACTOR,
  MINE_MINERAL,
  SELECT_MINERAL,
  UNSELECT_MINERAL,
  MINE_GAS,
  FIND_PLACE_FOR_SPIRE,
  FIND_PLACE_FOR_HYDRALISK_DEN,
  FIND_PLACE_FOR_CREEP_COLONY,
  FIND_PLACE_FOR_EVOLUTION_CHAMBER,
  BUILD,

  //desires for buildings
  UPDATE_BELIEFS_ABOUT_CONSTRUCTION,

  //scouting
  VISIT,
  WORKER_SCOUT,

  //units
  MOVE_AWAY_FROM_DANGER,
  MOVE_TO_POSITION,
  ATTACK;

  //building desires
  public static final Set<DesireKeys> BUILDING_DESIRE_KEYS = Stream.of(
      MORPH_TO_POOL, MORPH_TO_EXTRACTOR, MORPH_TO_SPIRE, MORPH_TO_HYDRALISK_DEN,
      MORPH_TO_CREEP_COLONY, MORPH_TO_EVOLUTION_CHAMBER
  ).collect(Collectors.toSet());

  //learnt desires
  public static final Set<DesireKeys> LEARNT_DESIRE_KEYS = Stream.of(
      EXPAND, BUILD_EXTRACTOR, BUILD_WORKER, INCREASE_CAPACITY, ENABLE_AIR,
      ENABLE_GROUND_RANGED, ENABLE_STATIC_ANTI_AIR, ENABLE_GROUND_MELEE, UPGRADE_TO_LAIR,
      BOOST_AIR, BOOST_GROUND_MELEE, BOOST_GROUND_RANGED, HOLD_GROUND, HOLD_AIR,
      BUILD_SUNKEN_COLONY, BUILD_SPORE_COLONY
  ).collect(Collectors.toSet());
  public static final DesireKeys values[] = values();

  public DesireKeyID getId() {
    return new DesireKeyID(this.name(), this.ordinal());
  }

  @Override
  public String toString() {
    return name();
  }
}
