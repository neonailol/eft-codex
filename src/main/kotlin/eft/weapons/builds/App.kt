package eft.weapons.builds

import com.fasterxml.jackson.annotation.JsonProperty

data class TestItemTemplates(
    @JsonProperty("err")
    val error: Double,
    @JsonProperty("errmsg")
    val errorMessage: String?,
    val data: TestItemTemplatesData
)

class TestItemTemplatesData : HashMap<String, TestItem>()

data class TestItem(
    @JsonProperty("_id")
    val id: String,
    @JsonProperty("_name")
    val name: String,
    @JsonProperty("_parent")
    val parent: String,
    @JsonProperty("_type")
    val type: String,
    @JsonProperty("_props")
    val props: TestItemProps?,
    @JsonProperty("_proto")
    val proto: String?
)

data class TestItemProps(
    val Name: String?,
    val ShortName: String?,
    val Description: String?,
    val Weight: Double?,
    val BackgroundColor: String?,
    val Width: Double?,
    val Height: Double?,
    val StackMaxSize: Double?,
    val Rarity: String?,
    val SpawnChance: Double?,
    val CreditsPrice: Double?,
    val ItemSound: String?,
    val StackObjectsCount: Double?,
    val NotShownInSlot: Boolean?,
    val ExaminedByDefault: Boolean?,
    val ExamineTime: Double?,
    val IsUnsaleable: Boolean?,
    val IsUndiscardable: Boolean?,
    val IsUnbuyable: Boolean?,
    val IsUngivable: Boolean?,
    val IsLockedafterEquip: Boolean?,
    val QuestItem: Boolean?,
    val LootExperience: Double?,
    val ExamineExperience: Double?,
    val HideEntrails: Boolean?,
    val RepairCost: Double?,
    val RepairSpeed: Double?,
    val ExtraSizeLeft: Double?,
    val ExtraSizeRight: Double?,
    val ExtraSizeUp: Double?,
    val ExtraSizeDown: Double?,
    val ExtraSizeForceAdd: Boolean?,
    val MergesWithChildren: Boolean?,
    val BannedFromRagfair: Boolean?,
    val FixedPrice: Boolean?,
    val Unlootable: Boolean?,
    val UnlootableFromSlot: String?,
    val ChangePriceCoef: Double?,
    val SendToClient: Boolean?,
    val AllowSpawnOnLocations: Collection<String>?,
    val UnlootableFromSide: Collection<String>?,
    val ConflictingItems: Collection<String>?,
    val Prefab: TestItemPrefab?,
    val UsePrefab: TestItemPrefab?,
    val DogTagQualities: Boolean?,
    val Grids: Collection<TestItemGrid>?,
    val Slots: Collection<TestItemSlot>?,
    val KeyIds: Collection<String>?,
    val TagColor: Double?,
    val TagName: String?,
    val Durability: Double?,
    val Accuracy: Double?,
    val Recoil: Double?,
    val Loudness: Double?,
    val EffectiveDistance: Double?,
    val Ergonomics: Double?,
    val Velocity: Double?,
    val RaidModdable: Boolean?,
    val ToolModdable: Boolean?,
    val BlocksFolding: Boolean?,
    val BlocksCollapsible: Boolean?,
    val IsAnimated: Boolean?,
    val HasShoulderContact: Boolean?,
    val SightingRange: Double?,
    val ModesCount: Double?,
    val muzzleModType: String?,
    val sightModType: String?,
    val variableZoom: Boolean?,
    val varZoomCount: Double?,
    val varZoomAdd: Double?,
    val aimingSensitivity: Double?,
    val SightModesCount: Double?,
    val OpticCalibrationDistances: Collection<Double>?,
    val Intensity: Double?,
    val Mask: String?,
    val MaskSize: Double?,
    val NoiseIntensity: Double?,
    val NoiseScale: Double?,
    val Color: TestItemPropsColor?,
    val DiffuseIntensity: Double?,
    val magAnimationIndex: Double?,
    val CanFast: Boolean?,
    val CanHit: Boolean?,
    val CanAdmin: Boolean?,
    val LoadUnloadModifier: Double?,
    val CheckTimeModifier: Double?,
    val CheckOverride: Double?,
    val ReloadMagType: String?,
    val Cartridges: Collection<TestItemPropsCartridges>?,
    val IsShoulderContact: Boolean?,
    val Foldable: Boolean?,
    val Retractable: Boolean?,
    val SizeReduceRight: Double?,
    val CenterOfImpact: Double?,
    val ShotgunDispersion: Double?,
    val IsSilencer: Boolean?,
    val SearchSound: String?,
    val BlocksArmorVest: Boolean?,
    val speedPenaltyPercent: Double?,
    val GridLayoutName: String?,
    val SpawnFilter: Collection<String>?,
    val containType: Collection<String>?,
    val sizeWidth: Double?,
    val sizeHeight: Double?,
    val isSecured: Boolean?,
    val spawnTypes: String?,
    val lootFilter: Collection<String>?,
    val spawnRarity: String?,
    val minCountSpawn: Double?,
    val maxCountSpawn: Double?,
    val openedByKeyID: Collection<String>?,
    val RigLayoutName: String?,
    val MaxDurability: Double?,
    val armorZone: Collection<String>?,
    val armorClass: Double?,
    val mousePenalty: Double?,
    val weaponErgonomicPenalty: Double?,
    val BluntThroughput: Double?,
    val ArmorMaterial: String?
)

/*
   "weapClass": "assaultRifle",
        "weapUseType": "primary",
        "ammoCaliber": "Caliber762x51",
        "Durability": 100,
        "MaxDurability": 100,
        "OperatingResource": 3000,
        "RepairComplexity": 0,
        "durabSpawnMin": 15,
        "durabSpawnMax": 65,
        "isFastReload": true,
        "RecoilForceUp": 300,
        "RecoilForceBack": 540,
        "Convergence": 1.5,
        "RecoilAngle": 85,
        "weapFireType": [
          "single"
        ],
        "RecolDispersion": 25,
        "bFirerate": 600,
        "Ergonomics": 29,
        "Velocity": 1.8,
        "bEffDist": 400,
        "bHearDist": 80,
        "isChamberLoad": true,
        "chamberAmmoCount": 1,
        "isBoltCatch": false,
        "defMagType": "5c503ac82e221602b21d6e9a",
        "defAmmo": "5a6086ea4f39f99cd479502f",
        "shotgunDispersion": 0,
        "Chambers": [
          {
            "_name": "patron_in_weapon",
            "_id": "5c501a4d2e221602b412b542",
            "_parent": "5c501a4d2e221602b412b540",
            "_props": {
              "filters": [
                {
                  "Filter": [
                    "5a6086ea4f39f99cd479502f",
                    "5a608bf24f39f98ffc77720e",
                    "58dd3ad986f77403051cba8f"
                  ]
                }
              ]
            },
            "_required": false,
            "_mergeSlotWithChildren": false,
            "_proto": "55d4af244bdc2d962f8b4571"
          }
        ],
        "CameraRecoil": 0.2,
        "CameraSnap": 3.5,
        "ReloadMode": "ExternalMagazine",
        "CenterOfImpact": 0.05,
        "AimPlane": 0.19,
        "DeviationCurve": 1,
        "DeviationMax": 100,
        "Foldable": false,
        "Retractable": false,
        "TacticalReloadStiffnes": {
          "x": 0.95,
          "y": 0.33,
          "z": 0.95
        },
        "TacticalReloadFixation": 0.95,
        "RecoilCenter": {
          "x": 0,
          "y": -0.25,
          "z": 0
        },
        "RotationCenter": {
          "x": 0,
          "y": -0.1,
          "z": -0.03
        },
        "RotationCenterNoStock": {
          "x": 0,
          "y": -0.27,
          "z": -0.08
        },
        "SizeReduceRight": 0,
        "FoldedSlot": "",
        "CompactHandling": true,
        "SightingRange": 100,
        "MinRepairDegradation": 0,
        "MaxRepairDegradation": 0.01,
        "IronSightRange": 100,
        "MustBoltBeOpennedForExternalReload": false,
        "MustBoltBeOpennedForInternalReload": false,
        "BoltAction": false,
        "HipAccuracyRestorationDelay": 0.2,
        "HipAccuracyRestorationSpeed": 7,
        "HipInnaccuracyGain": 0.16,
        "ManualBoltCatch": false
 */

data class TestItemPropsCartridges(
    @JsonProperty("_id")
    val id: String,
    @JsonProperty("_name")
    val name: String,
    @JsonProperty("_parent")
    val parent: String,
    @JsonProperty("_proto")
    val proto: String,
    @JsonProperty("_max_count")
    val maxCount: Double,
    @JsonProperty("_props")
    val props: TestItemPropsCartridgesProps
)

data class TestItemPropsCartridgesProps(
    val filters: Collection<TestItemPropsCartridgesPropsFilter>
)

data class TestItemPropsCartridgesPropsFilter(
    @JsonProperty("Filter")
    val filter: Collection<String>
)

data class TestItemPropsColor(
    val r: Double,
    val g: Double,
    val b: Double,
    val a: Double
)

data class TestItemSlot(
    @JsonProperty("_id")
    val id: String,
    @JsonProperty("_name")
    val name: String,
    @JsonProperty("_parent")
    val parent: String,
    @JsonProperty("_required")
    val required: Boolean,
    @JsonProperty("_mergeSlotWithChildren")
    val merge: Boolean,
    @JsonProperty("_proto")
    val proto: String,
    @JsonProperty("_props")
    val props: TestItemSlotProps
)

data class TestItemSlotProps(
    val filters: Collection<TestItemSlotPropsFilter>
)

data class TestItemSlotPropsFilter(
    val Shift: Double?,
    val AnimationIndex: Double?,
    val Filter: Collection<String>
)

data class TestItemGrid(
    @JsonProperty("_id")
    val id: String,
    @JsonProperty("_name")
    val name: String,
    @JsonProperty("_parent")
    val parent: String,
    @JsonProperty("_required")
    val required: Boolean,
    @JsonProperty("_mergeSlotWithChildren")
    val merge: Boolean,
    @JsonProperty("_proto")
    val proto: String,
    @JsonProperty("_props")
    val props: TestItemGridProps
)

data class TestItemGridProps(
    val filters: Collection<TestItemGridPropsFilter>,
    val cellsH: Double,
    val cellsV: Double,
    val minCount: Double,
    val maxCount: Double,
    val maxWeight: Double
)

data class TestItemGridPropsFilter(
    @JsonProperty("Filter")
    val filter: Collection<String>
)

data class TestItemPrefab(
    val path: String,
    val rcid: String
)

fun main(args: Array<String>) {
    println(args)
}
