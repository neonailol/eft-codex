
package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

class TestItemTemplatesItem {
    var _props: TestItemTemplatesItemProps? = null
    var _proto: String? = null
    var _id: String? = null
    var _type: String? = null
    var _name: String? = null
    var _parent: String? = null
}

@JsonIgnoreProperties("Buffs")
class TestItemTemplatesItemProps {
    var apResource: Int? = null
    var Deterioration: Int? = null
    @JsonProperty("Unlootable")
    var Unlootable: Boolean? = null
    var PenetrationPower: Int? = null
    @JsonProperty("RaidModdable")
    var RaidModdable: Boolean? = null
    var knifeHitSlashRate: Int? = null
    var Prefab: TestItemTemplatesItemPropsPrefab? = null
    var PrimaryDistance: Double? = null
    var StackMaxSize: Int? = null
    var UsePrefab: TestItemTemplatesItemPropsUsePrefab? = null
    var ConfigPathStr: String? = null
    var DeviationCurve: Int? = null
    var CheckTimeModifier: Int? = null
    var ammoShiftChance: Int? = null
    var aimingSensitivity: Double? = null
    var Caliber: String? = null
    @JsonProperty("FixedPrice")
    var FixedPrice: Boolean? = null
    var muzzleModType: String? = null
    var SlashPenetration: Int? = null
    @JsonProperty("isSecured")
    var isSecured: Boolean? = null
    var SightModesCount: Int? = null
    var Grids: Collection<TestItemTemplatesItemPropsGrids>? = null
    var medUseTime: Int? = null
    @JsonProperty("IsSilencer")
    var IsSilencer: Boolean? = null
    var effects_speed: TestItemTemplatesItemPropsEffectsSpeed? = null
    @JsonProperty("isChamberLoad")
    var isChamberLoad: Boolean? = null
    var Accuracy: Int? = null
    var HipAccuracyRestorationDelay: Double? = null
    var MaxFragmentsCount: Int? = null
    var UnlootableFromSlot: String? = null
    var Slots: Collection<TestItemTemplatesItemPropsSlots>? = null
    var casingMass: Double? = null
    var varZoomCount: Int? = null
    var sizeHeight: Int? = null
    var casingSounds: String? = null
    var PenetrationChance: Double? = null
    @JsonProperty("SendToClient")
    var SendToClient: Boolean? = null
    var CameraSnap: Double? = null
    @JsonProperty("Foldable")
    var Foldable: Boolean? = null
    @JsonProperty("CanHit")
    var CanHit: Boolean? = null
    var ReloadMagType: String? = null
    var defAmmo: String? = null
    var Chambers: Collection<TestItemTemplatesItemPropsChambers>? = null
    var varZoomAdd: Int? = null
    var GridLayoutName: String? = null
    var OverdoseRecovery: Int? = null
    var ammoCaliber: String? = null
    var eqMin: Int? = null
    var throwDamMax: Int? = null
    var casingName: String? = null
    var CreditsPrice: Int? = null
    var weapUseType: String? = null
    var Overdose: Int? = null
    var BallisticCoeficient: Double? = null
    var foodEffectType: String? = null
    @JsonProperty("IsShoulderContact")
    var IsShoulderContact: Boolean? = null
    var ammoRec: Int? = null
    var ammoHear: Int? = null
    var KeyIds: Collection<String>? = null
    var MaxResource: Int? = null
    var SpeedRetardation: Double? = null
    var UnlootableFromSide: Collection<String>? = null
    var ammoSfx: String? = null
    var RigLayoutName: String? = null
    var MinExplosionDistance: Double? = null
    var spawnRarity: String? = null
    var ConflictingItems: Collection<String>? = null
    @JsonProperty("Tracer")
    var Tracer: Boolean? = null
    @JsonProperty("IsAnimated")
    var IsAnimated: Boolean? = null
    var FoldedSlot: String? = null
    @JsonProperty("DogTagQualities")
    var DogTagQualities: Boolean? = null
    @JsonProperty("IsUnbuyable")
    var IsUnbuyable: Boolean? = null
    var knifeHitSlashDam: Int? = null
    var Description: String? = null
    var knifeHitDelay: Int? = null
    var rate: Int? = null
    var CompressorAttack: Int? = null
    var ReloadMode: String? = null
    var mousePenalty: Int? = null
    var OpticCalibrationDistances: Collection<Long>? = null
    var DryVolume: Int? = null
    var Width: Int? = null
    @JsonProperty("CanAdmin")
    var CanAdmin: Boolean? = null
    var medEffectType: String? = null
    var RicochetChance: Double? = null
    var TracerDistance: Double? = null
    var RotationCenter: TestItemTemplatesItemPropsRotationCenter? = null
    var RepairComplexity: Int? = null
    var RicochetParams: TestItemTemplatesItemPropsRicochetParams? = null
    var ExtraSizeDown: Int? = null
    var EffectiveDistance: Int? = null
    var ArmorMaterial: String? = null
    var StaminaBurnPerDamage: Double? = null
    var Resonance: Double? = null
    var Loudness: Int? = null
    @JsonProperty("QuestItem")
    var QuestItem: Boolean? = null
    var ExtraSizeUp: Int? = null
    var AllowSpawnOnLocations: Collection<String>? = null
    var armorClass: String? = null
    @JsonProperty("CompactHandling")
    var CompactHandling: Boolean? = null
    var SecondryConsumption: Int? = null
    var speedPenaltyPercent: Int? = null
    var CompressorGain: Int? = null
    var PenetrationPowerDiviation: Double? = null
    var IronSightRange: Int? = null
    @JsonProperty("NotShownInSlot")
    var NotShownInSlot: Boolean? = null
    @JsonProperty("CanFast")
    var CanFast: Boolean? = null
    var ExamineExperience: Int? = null
    var BluntThroughput: Double? = null
    var ammoType: String? = null
    var RepairSpeed: Int? = null
    @JsonProperty("IsUndiscardable")
    var IsUndiscardable: Boolean? = null
    @JsonProperty("MergesWithChildren")
    var MergesWithChildren: Boolean? = null
    var ModesCount: Int? = null
    var FragmentsCount: Int? = null
    var TracerColor: String? = null
    var RecolDispersion: Int? = null
    var LoadUnloadModifier: Int? = null
    var NoiseScale: Int? = null
    var Contusion: TestItemTemplatesItemPropsContusion? = null
    var AmbientVolume: Int? = null
    var BackgroundColor: String? = null
    var MaskSize: Double? = null
    var headSegments: Collection<String>? = null
    @JsonProperty("HideEntrails")
    var HideEntrails: Boolean? = null
    var MinRepairDegradation: Int? = null
    @JsonProperty("IsUnsaleable")
    var IsUnsaleable: Boolean? = null
    var MinFragmentsCount: Int? = null
    var SearchSound: String? = null
    var sizeWidth: Int? = null
    @JsonProperty("BoltAction")
    var BoltAction: Boolean? = null
    var casingEjectPower: Int? = null
    var SightingRange: Int? = null
    var CutoffFreq: Int? = null
    var OperatingResource: Int? = null
    var ExtraSizeLeft: Int? = null
    var PrimaryConsumption: Int? = null
    var knifeHitStabRate: Int? = null
    var bFirerate: Int? = null
    var RepairCost: Int? = null
    var sightModType: String? = null
    var ShortName: String? = null
    var scaleMin: Double? = null
    var knifeHitStabDam: Int? = null
    var Velocity: Double? = null
    var containType: Collection<String>? = null
    var HipInnaccuracyGain: Double? = null
    var Indestructibility: Double? = null
    var magAnimationIndex: Int? = null
    var Color: TestItemTemplatesItemPropsColor? = null
    var CenterOfImpact: Double? = null
    var Rarity: String? = null
    var RecoilCenter: TestItemTemplatesItemPropsRecoilCenter? = null
    var MaxRepairDegradation: Double? = null
    var maxCountSpawn: Int? = null
    var DeviationMax: Int? = null
    var TagName: String? = null
    var StimulatorBuffs: String? = null
    @JsonProperty("HasHinge")
    var HasHinge: Boolean? = null
    var TacticalReloadStiffnes: TestItemTemplatesItemPropsTacticalReloadStiffnes? = null
    @JsonProperty("CanBeHiddenDuringThrow")
    var CanBeHiddenDuringThrow: Boolean? = null
    var Ergonomics: Double? = null
    var Distortion: Double? = null
    var MaximumNumberOfUsage: Int? = null
    var EmitTime: Int? = null
    var effects_health: TestItemTemplatesItemPropsEffectsHealth? = null
    var SpawnFilter: Collection<String>? = null
    var spawnTypes: String? = null
    var Name: String? = null
    var TagColor: Int? = null
    var Durability: Int? = null
    var knifeHitRadius: Double? = null
    @JsonProperty("ExtraSizeForceAdd")
    var ExtraSizeForceAdd: Boolean? = null
    var effects_damage: TestItemTemplatesItemPropsEffectsDamage? = null
    @JsonProperty("BannedFromRagfair")
    var BannedFromRagfair: Boolean? = null
    @JsonProperty("BlocksFolding")
    var BlocksFolding: Boolean? = null
    var StabPenetration: Int? = null
    var SpawnChance: Double? = null
    var TacticalReloadFixation: Double? = null
    var Intensity: Double? = null
    var DeafStrength: String? = null
    var CheckOverride: Int? = null
    @JsonProperty("BlocksFaceCover")
    var BlocksFaceCover: Boolean? = null
    var HipAccuracyRestorationSpeed: Int? = null
    var ammoAccr: Int? = null
    var ProjectileCount: Int? = null
    @JsonProperty("BlocksHeadwear")
    var BlocksHeadwear: Boolean? = null
    @JsonProperty("isFastReload")
    var isFastReload: Boolean? = null
    var defMagType: String? = null
    var ContusionDistance: Int? = null
    var RecoilForceBack: Int? = null
    var Blindness: TestItemTemplatesItemPropsBlindness? = null
    @JsonProperty("variableZoom")
    var variableZoom: Boolean? = null
    var MaxDurability: Int? = null
    var Height: Int? = null
    var ThrowType: String? = null
    var ExplDelay: Int? = null
    var CompressorVolume: Int? = null
    var SizeReduceRight: Int? = null
    var StackObjectsCount: Int? = null
    var MaxHpResource: Int? = null
    var MaxMarkersCount: Int? = null
    var SecondryDistance: Double? = null
    var durabSpawnMax: Int? = null
    var ammoDist: Int? = null
    var InitialSpeed: Int? = null
    var RotationCenterNoStock: TestItemTemplatesItemPropsRotationCenterNoStock? = null
    var RecoilForceUp: Int? = null
    var Convergence: Double? = null
    var Cartridges: Collection<TestItemTemplatesItemPropsCartridges>? = null
    var ChangePriceCoef: Int? = null
    @JsonProperty("BlocksEarpiece")
    var BlocksEarpiece: Boolean? = null
    var MisfireChance: Double? = null
    var weaponErgonomicPenalty: Int? = null
    @JsonProperty("HasShoulderContact")
    var HasShoulderContact: Boolean? = null
    var weapFireType: Collection<String>? = null
    var Mask: String? = null
    var RecoilAngle: Int? = null
    var StackMaxRandom: Int? = null
    var Recoil: Double? = null
    var foodUseTime: Int? = null
    var DeflectionConsumption: Int? = null
    var FaceShieldMask: String? = null
    @JsonProperty("ExaminedByDefault")
    var ExaminedByDefault: Boolean? = null
    var knifeDurab: Int? = null
    var scaleMax: Double? = null
    @JsonProperty("Retractable")
    var Retractable: Boolean? = null
    @JsonProperty("isBoltCatch")
    var isBoltCatch: Boolean? = null
    var MaterialType: String? = null
    @JsonProperty("BlocksCollapsible")
    var BlocksCollapsible: Boolean? = null
    var weapClass: String? = null
    var Addiction: Int? = null
    @JsonProperty("IsUngivable")
    var IsUngivable: Boolean? = null
    var MaxExplosionDistance: Int? = null
    var lootFilter: Collection<String>? = null
    var NoiseIntensity: Double? = null
    var ExamineTime: Int? = null
    var minCountSpawn: Int? = null
    var CompressorTreshold: Int? = null
    var Damage: Int? = null
    var openedByKeyID: Collection<String>? = null
    @JsonProperty("BlocksEyewear")
    var BlocksEyewear: Boolean? = null
    var MaxEfficiency: Int? = null
    var StackSlots: Collection<TestItemTemplatesItemPropsStackSlots>? = null
    var eqMax: Int? = null
    @JsonProperty("ToolModdable")
    var ToolModdable: Boolean? = null
    var CameraRecoil: Double? = null
    @JsonProperty("IsLockedafterEquip")
    var IsLockedafterEquip: Boolean? = null
    var buckshotBullets: Int? = null
    var Strength: Int? = null
    var StackMinRandom: Int? = null
    var LootExperience: Int? = null
    @JsonProperty("BlocksArmorVest")
    var BlocksArmorVest: Boolean? = null
    var armorZone: Collection<String>? = null
    var hpResourceRate: Int? = null
    var FragmentType: String? = null
    var FragmentationChance: Double? = null
    @JsonProperty("FaceShieldComponent")
    var FaceShieldComponent: Boolean? = null
    var AddictionRecovery: Int? = null
    @JsonProperty("ManualBoltCatch")
    var ManualBoltCatch: Boolean? = null
    var type: String? = null
    var bHearDist: Int? = null
    @JsonProperty("MustBoltBeOpennedForInternalReload")
    var MustBoltBeOpennedForInternalReload: Boolean? = null
    var CompressorRelease: Int? = null
    var chamberAmmoCount: Int? = null
    var ExtraSizeRight: Int? = null
    var ShotgunDispersion: Double? = null
    var durabSpawnMin: Int? = null
    @JsonProperty("MustBoltBeOpennedForExternalReload")
    var MustBoltBeOpennedForExternalReload: Boolean? = null
    var krResource: Int? = null
    var DiffuseIntensity: Double? = null
    var ItemSound: String? = null
    var AimPlane: Double? = null
    var Weight: Double? = null
    var bEffDist: Int? = null
    var ArmorDamage: Int? = null
}

class TestItemTemplatesItemPropsBlindness {
    var x: Int? = null
    var y: Int? = null
    var z: Int? = null
}

class TestItemTemplatesItemPropsBuffs {
    var Skill: String? = null
    var FadeOut: Int? = null
    var PlatoValue: Int? = null
    var FadeIn: Int? = null
    var Plato: Int? = null
    var Vitality: TestItemTemplatesItemPropsBuffsVitality? = null
}

class TestItemTemplatesItemPropsBuffsVitality {
    var FadeOut: Int? = null
    var PlatoValue: Int? = null
    var Plato: Int? = null
    var FadeIn: Int? = null
}

class TestItemTemplatesItemPropsCartridges {
    var _props: TestItemTemplatesItemPropsCartridgesProps? = null
    var _parent: String? = null
    var _proto: String? = null
    var _max_count: Int? = null
    var _name: String? = null
    var _id: String? = null
}

class TestItemTemplatesItemPropsCartridgesProps {
    var filters: Collection<TestItemTemplatesItemPropsCartridgesPropsFilters>? = null
}

class TestItemTemplatesItemPropsCartridgesPropsFilters {
    var Filter: Collection<String>? = null
}

class TestItemTemplatesItemPropsChambers {
    var _props: TestItemTemplatesItemPropsChambersProps? = null
    var _id: String? = null
    var _proto: String? = null
    var _name: String? = null
    @JsonProperty("_required")
    var _required: Boolean? = null
    var _parent: String? = null
    @JsonProperty("_mergeSlotWithChildren")
    var _mergeSlotWithChildren: Boolean? = null
}

class TestItemTemplatesItemPropsChambersProps {
    var filters: Collection<TestItemTemplatesItemPropsChambersPropsFilters>? = null
}

class TestItemTemplatesItemPropsChambersPropsFilters {
    var Filter: Collection<String>? = null
    var MaxStackCount: Int? = null
}

class TestItemTemplatesItemPropsColor {
    var r: Int? = null
    var g: Int? = null
    var a: Int? = null
    var b: Int? = null
}

class TestItemTemplatesItemPropsContusion {
    var z: Int? = null
    var y: Int? = null
    var x: Double? = null
}

class TestItemTemplatesItemPropsGrids {
    var _name: String? = null
    var _id: String? = null
    var _props: TestItemTemplatesItemPropsGridsProps? = null
    var _parent: String? = null
    var _proto: String? = null
}

class TestItemTemplatesItemPropsGridsProps {
    var minCount: Int? = null
    var filters: Collection<TestItemTemplatesItemPropsGridsPropsFilters>? = null
    var cellsV: Int? = null
    var cellsH: Int? = null
    var maxCount: Int? = null
    var maxWeight: Int? = null
}

class TestItemTemplatesItemPropsGridsPropsFilters {
    var Filter: Collection<String>? = null
}

class TestItemTemplatesItemPropsPrefab {
    var rcid: String? = null
    var path: String? = null
}

class TestItemTemplatesItemPropsRecoilCenter {
    var x: Int? = null
    var y: Double? = null
    var z: Double? = null
}

class TestItemTemplatesItemPropsRicochetParams {
    var y: Double? = null
    var z: Int? = null
    var x: Double? = null
}

class TestItemTemplatesItemPropsRotationCenter {
    var y: Double? = null
    var z: Double? = null
    var x: Int? = null
}

class TestItemTemplatesItemPropsRotationCenterNoStock {
    var x: Int? = null
    var y: Double? = null
    var z: Double? = null
}

class TestItemTemplatesItemPropsSlots {
    var _name: String? = null
    @JsonProperty("_mergeSlotWithChildren")
    var _mergeSlotWithChildren: Boolean? = null
    var _parent: String? = null
    var _props: TestItemTemplatesItemPropsSlotsProps? = null
    @JsonProperty("_required")
    var _required: Boolean? = null
    var _proto: String? = null
    var _id: String? = null
}

class TestItemTemplatesItemPropsSlotsProps {
    var filters: Collection<TestItemTemplatesItemPropsSlotsPropsFilters>? = null
}

class TestItemTemplatesItemPropsSlotsPropsFilters {
    var Shift: Int? = null
    var Filter: Collection<String>? = null
    var AnimationIndex: Int? = null
}

class TestItemTemplatesItemPropsStackSlots {
    var _name: String? = null
    var _id: String? = null
    var _props: TestItemTemplatesItemPropsStackSlotsProps? = null
    var _parent: String? = null
    var _max_count: Int? = null
    var _proto: String? = null
}

class TestItemTemplatesItemPropsStackSlotsProps {
    var filters: Collection<TestItemTemplatesItemPropsStackSlotsPropsFilters>? = null
}

class TestItemTemplatesItemPropsStackSlotsPropsFilters {
    var Filter: Collection<String>? = null
}

class TestItemTemplatesItemPropsTacticalReloadStiffnes {
    var z: Double? = null
    var x: Double? = null
    var y: Double? = null
}

class TestItemTemplatesItemPropsUsePrefab {
    var rcid: String? = null
    var path: String? = null
}

class TestItemTemplatesItemPropsEffectsDamage {
    var fracture: TestItemTemplatesItemPropsEffectsDamageFracture? = null
    var radExposure: TestItemTemplatesItemPropsEffectsDamageRadExposure? = null
    var pain: TestItemTemplatesItemPropsEffectsDamagePain? = null
    var bloodloss: TestItemTemplatesItemPropsEffectsDamageBloodloss? = null
    var contusion: TestItemTemplatesItemPropsEffectsDamageContusion? = null
    var toxication: TestItemTemplatesItemPropsEffectsDamageToxication? = null
}

class TestItemTemplatesItemPropsEffectsDamageBloodloss {
    var duration: Int? = null
    @JsonProperty("remove")
    var remove: Boolean? = null
    var time: Int? = null
    var cost: Int? = null
    var fadeOut: Int? = null
}

class TestItemTemplatesItemPropsEffectsDamageContusion {
    @JsonProperty("remove")
    var remove: Boolean? = null
    var duration: Int? = null
    var fadeOut: Int? = null
    var time: Int? = null
    var cost: Int? = null
}

class TestItemTemplatesItemPropsEffectsDamageFracture {
    var fadeOut: Int? = null
    var duration: Int? = null
    var time: Int? = null
    var cost: Int? = null
    @JsonProperty("remove")
    var remove: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsDamagePain {
    var time: Int? = null
    var cost: Int? = null
    var duration: Int? = null
    @JsonProperty("remove")
    var remove: Boolean? = null
    var fadeOut: Int? = null
}

class TestItemTemplatesItemPropsEffectsDamageRadExposure {
    var time: Int? = null
    var fadeOut: Int? = null
    var duration: Int? = null
    var cost: Int? = null
    @JsonProperty("remove")
    var remove: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsDamageToxication {
    @JsonProperty("remove")
    var remove: Boolean? = null
    var cost: Int? = null
    var fadeOut: Int? = null
    var duration: Int? = null
    var time: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealth {
    var leg_left: TestItemTemplatesItemPropsEffectsHealthLegLeft? = null
    var chest: TestItemTemplatesItemPropsEffectsHealthChest? = null
    var energy: TestItemTemplatesItemPropsEffectsHealthEnergy? = null
    var arm_right: TestItemTemplatesItemPropsEffectsHealthArmRight? = null
    var arm_left: TestItemTemplatesItemPropsEffectsHealthArmLeft? = null
    var head: TestItemTemplatesItemPropsEffectsHealthHead? = null
    var leg_right: TestItemTemplatesItemPropsEffectsHealthLegRight? = null
    var common: TestItemTemplatesItemPropsEffectsHealthCommon? = null
    var hydration: TestItemTemplatesItemPropsEffectsHealthHydration? = null
    var tummy: TestItemTemplatesItemPropsEffectsHealthTummy? = null
}

class TestItemTemplatesItemPropsEffectsHealthArmLeft {
    @JsonProperty("percent")
    var percent: Boolean? = null
    var value: Int? = null
    var duration: Int? = null
    var time: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthArmRight {
    var duration: Int? = null
    var time: Int? = null
    var value: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsHealthChest {
    var value: Int? = null
    var duration: Int? = null
    var time: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsHealthCommon {
    @JsonProperty("percent")
    var percent: Boolean? = null
    var duration: Int? = null
    var value: Int? = null
    var time: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthEnergy {
    @JsonProperty("percent")
    var percent: Boolean? = null
    var value: Int? = null
    var time: Int? = null
    var duration: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthHead {
    var time: Int? = null
    var duration: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var value: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthHydration {
    var value: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var time: Int? = null
    var duration: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthLegLeft {
    var time: Int? = null
    var value: Int? = null
    var duration: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsHealthLegRight {
    var duration: Int? = null
    var time: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var value: Int? = null
}

class TestItemTemplatesItemPropsEffectsHealthTummy {
    var value: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var time: Int? = null
    var duration: Int? = null
}

class TestItemTemplatesItemPropsEffectsSpeed {
    var unlockSpeed: TestItemTemplatesItemPropsEffectsSpeedUnlockSpeed? = null
    var mobility: TestItemTemplatesItemPropsEffectsSpeedMobility? = null
    var lootSpeed: TestItemTemplatesItemPropsEffectsSpeedLootSpeed? = null
    var recoil: TestItemTemplatesItemPropsEffectsSpeedRecoil? = null
    var reloadSpeed: TestItemTemplatesItemPropsEffectsSpeedReloadSpeed? = null
}

class TestItemTemplatesItemPropsEffectsSpeedLootSpeed {
    var value: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var duration: Int? = null
    var time: Int? = null
}

class TestItemTemplatesItemPropsEffectsSpeedMobility {
    var value: Int? = null
    var duration: Int? = null
    var time: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsSpeedRecoil {
    var time: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var duration: Int? = null
    var value: Int? = null
}

class TestItemTemplatesItemPropsEffectsSpeedReloadSpeed {
    var value: Int? = null
    var duration: Int? = null
    var time: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
}

class TestItemTemplatesItemPropsEffectsSpeedUnlockSpeed {
    var value: Int? = null
    @JsonProperty("percent")
    var percent: Boolean? = null
    var time: Int? = null
    var duration: Int? = null
}
