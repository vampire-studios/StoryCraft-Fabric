package io.github.vampirestudios.storyterium.entity;

import io.github.vampirestudios.questing_api.QuestManager;
import io.github.vampirestudios.questing_api.api.Quest;
import io.github.vampirestudios.storyterium.entity.ai.goal.FindDiamondBlockGoal;
import io.github.vampirestudios.storyterium.entity.ai.goal.VillagerFarmGoal;
import io.github.vampirestudios.storyterium.entity.ai.goal.VillagerStareGoal;
import io.github.vampirestudios.storyterium.gui.BaseScreen;
import io.github.vampirestudios.storyterium.gui.FamiliarsScreen;
import io.github.vampirestudios.storyterium.init.SCCustomTrackedData;
import io.github.vampirestudios.storyterium.main.Storyterium;
import io.github.vampirestudios.storyterium.socialVillager.FamiliarsAspects;
import io.github.vampirestudios.storyterium.socialVillager.FamiliarsGender;
import io.github.vampirestudios.storyterium.socialVillager.FamiliarsProfession;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class FamiliarsEntity extends PassiveEntity {
    public static final EntityAttribute MAX_HUNGER = new ClampedEntityAttribute("generic.hunger", 100.0D, 0.0D, 100.0D)/*.setName("Hunger")*/.setTracked(true);
    public static final EntityAttribute MAX_HAPPY = (new ClampedEntityAttribute("generic.happy", 100.0D, 0.0D, 100.0D))/*.setName("Happy")*/.setTracked(true);
    public static final EntityAttribute MAX_INTELLIGENCE = (new ClampedEntityAttribute("generic.intelligence", 100.0D, 0.0D, 100.0D))/*.setName("Intelligence")*/.setTracked(true);

    private static final TrackedData<Integer> HUNGER = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> HAPPY = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INTELLIGENCE = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> ACTION_ITEM = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public static final TrackedData<FamiliarsAspects> ASPECTS = DataTracker.registerData(FamiliarsEntity.class, SCCustomTrackedData.ASPECTS);
    public static final TrackedData<FamiliarsGender> GENDER = DataTracker.registerData(FamiliarsEntity.class, SCCustomTrackedData.GENDER);
    public static final TrackedData<FamiliarsProfession> PROFESSION = DataTracker.registerData(FamiliarsEntity.class, SCCustomTrackedData.PROFESSION);

    public static TrackedData<String> hairColorUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> eyeColorUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> skinColorUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<Integer> hairStyleUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static TrackedData<String> serverUUID = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> genderUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> professionUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    private static TrackedData<String> orientationUnified = DataTracker.registerData(FamiliarsEntity.class, TrackedDataHandlerRegistry.STRING);
    private final SimpleInventory inventory = new SimpleInventory(8);
    public String firstName;
    public String lastName;
    private HashMap<UUID, Integer> opinions = new HashMap<>();
    private String hairColor;
    private String eyeColor;
    private String skinColor;
    private String sexuality;
    private String gender;
    private Identifier profession;
    private int hairStyle = 0;
    private int friendliness = 0;
    private int bravery = 0;
    private int generosity = 0;
    private boolean apologized = false;
    private boolean charmed = false;
    private boolean goalsSet;
    private boolean staring;
    private boolean working;
    private boolean sleeping;

    public static int SLEEP_DURATION = 8000;
    public static int SLEEP_START_TIME = 16000;
    public static int SLEEP_END_TIME = SLEEP_START_TIME + SLEEP_DURATION;
    public static int WORK_START_TIME = 500;
    public static int WORK_END_TIME = 11500;
    private static int[] recentEatPenalties = {2, 0, -3, -7, -12, -18};

    protected BlockPos bedPos = null;
    protected BlockPos homeFrame = null;
    protected int sleepOffset = 0;
    protected int lastSadTick = 0;
    protected int lastSadThrottle = 200;
    protected int daysAlive = 0;

    private FamiliarsAspects familiarsAspects = new FamiliarsAspects(this);
    private FamiliarsProfession familiarsProfession = new FamiliarsProfession();
    private FamiliarsGender familiarsGender = new FamiliarsGender();

    public FamiliarsEntity(World world) {
        this(Storyterium.FAMILIARS, world);
    }

    private FamiliarsEntity(EntityType<? extends PassiveEntity> type, World world) {
        super(type, world);
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);
        this.setCanPickUpLoot(true);
        if (hairColor == null || hairColor.equals("")) {
            unifiedSetup();
            this.dataTracker.set(hairColorUnified, hairColor);
            this.dataTracker.set(eyeColorUnified, eyeColor);
            this.dataTracker.set(skinColorUnified, skinColor);
            this.dataTracker.set(hairStyleUnified, hairStyle);
            this.dataTracker.set(orientationUnified, sexuality);
            this.dataTracker.set(serverUUID, this.getUuidAsString());
            this.dataTracker.set(genderUnified, gender);
            this.dataTracker.set(professionUnified, profession.toString());
        }

        try {
            this.firstName = generateFirstName(this.get(genderUnified));
            this.lastName = generateLastName();
            this.setCustomName(new LiteralText(firstName + " " + lastName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 0.6D));
        this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(9, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.add(5, new FindDiamondBlockGoal(this, 1.0D));
        this.setSpecificGoals();
    }

    private void setSpecificGoals() {
        if (!this.goalsSet) {
            this.goalsSet = true;
            if (this.isBaby()) {
                this.goalSelector.add(8, new VillagerStareGoal(this, 0.32D));
            } else {
                this.goalSelector.add(6, new VillagerFarmGoal(this, 0.6D));
            }
            if (this.get(professionUnified).equals("Guard")) {
                this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
                this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
                this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
                this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
                this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
                this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
                this.goalSelector.add(8, new LookAroundGoal(this));
                this.targetSelector.add(2, new RevengeGoal(this));
                this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity_1) ->
                        livingEntity_1 instanceof Monster && !(livingEntity_1 instanceof CreeperEntity)));
            } else {
                this.goalSelector.add(1, new FleeEntityGoal<>(this, ZombieEntity.class, 8.0F, 0.6D, 0.6D));
                this.goalSelector.add(1, new FleeEntityGoal<>(this, EvokerEntity.class, 12.0F, 0.8D, 0.8D));
                this.goalSelector.add(1, new FleeEntityGoal<>(this, VindicatorEntity.class, 8.0F, 0.8D, 0.8D));
                this.goalSelector.add(1, new FleeEntityGoal<>(this, VexEntity.class, 8.0F, 0.6D, 0.6D));
                this.goalSelector.add(1, new FleeEntityGoal<>(this, PillagerEntity.class, 15.0F, 0.6D, 0.6D));
                this.goalSelector.add(1, new FleeEntityGoal<>(this, IllusionerEntity.class, 12.0F, 0.6D, 0.6D));
            }
        }
    }

    protected void onGrowUp() {
        if (this.profession.equals("Farmer")) {
            this.goalSelector.add(8, new VillagerFarmGoal(this, 0.6D));
        } else if (this.profession.equals("Guard")) {
            this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
            this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
            this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
            this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
            this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
            this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
            this.goalSelector.add(8, new LookAroundGoal(this));
            this.targetSelector.add(2, new RevengeGoal(this));
            this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity_1) ->
                    livingEntity_1 instanceof Monster && !(livingEntity_1 instanceof CreeperEntity)));
        }

        super.onGrowUp();
    }

    public boolean isStaring() {
        return this.staring;
    }

    public void setStaring(boolean boolean_1) {
        this.staring = boolean_1;
    }

    public boolean isWorking() {
        return working;
    }

    public FamiliarsEntity setWorking(boolean working) {
        this.working = working;
        return this;
    }

    @Override
    public boolean isSleeping() {
        return sleeping;
    }

    public FamiliarsEntity setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
        return this;
    }

    /*@Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }*/

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public boolean canBreed() {
        boolean isFarmer = this.profession.equals("Farmer");
        if (isFarmer) {
            return !this.hasEnoughFood(5);
        } else {
            return !this.hasEnoughFood(1);
        }
    }

    private boolean hasEnoughFood(int i1) {
        boolean isFarmer = this.profession.equals("Farmer");

        for (int i = 0; i < this.getInventory().getMaxCountPerStack(); ++i) {
            ItemStack stack = this.getInventory().getStack(i);
            Item item = stack.getItem();
            int count = stack.getCount();
            if (item == Items.BREAD && count >= 3 * i1 || item == Items.POTATO && count >= 12 * i1 || item == Items.CARROT && count >= 12 * i1 || item == Items.BEETROOT && count >= 12 * i1) {
                return true;
            }

            if (isFarmer && item == Items.WHEAT && count >= 9 * i1) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSeed() {
        for (int int_1 = 0; int_1 < this.getInventory().getMaxCountPerStack(); ++int_1) {
            Item item_1 = this.getInventory().getStack(int_1).getItem();
            if (item_1 == Items.WHEAT_SEEDS || item_1 == Items.POTATO || item_1 == Items.CARROT || item_1 == Items.BEETROOT_SEEDS) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(hairColorUnified, "Brown");
        this.dataTracker.startTracking(eyeColorUnified, "Black");
        this.dataTracker.startTracking(skinColorUnified, "Light");
        this.dataTracker.startTracking(hairStyleUnified, 1);
        this.dataTracker.startTracking(orientationUnified, "Straight");
        this.dataTracker.startTracking(serverUUID, this.getUuidAsString());
        this.dataTracker.startTracking(genderUnified, "Female");
        this.dataTracker.startTracking(professionUnified, "Nomad");
    }

    public <T> T get(TrackedData<T> key) {
        return this.dataTracker.get(key);
    }

    public <T> void set(TrackedData<T> key, T value) {
        this.dataTracker.set(key, value);
    }

    public void setOpinion(UUID uuid, int newValue) {
        this.opinions.put(uuid, newValue);
    }

    public int getOpinion(UUID uuid) {
        return opinions.get(uuid);
    }

    public boolean getApologized() {
        return apologized;
    }

    public void setApologized() {
        this.apologized = true;
    }

    private void formOpinion(Entity person) {
        if (!opinions.containsKey(person.getUuid())) {
            opinions.put(person.getUuid(), getRandom().nextInt(50) - 25);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!opinions.containsKey(player.getUuid())) {
            formOpinion(player);
        }
        Quest[] quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(getFamiliarsProfession().getProfession())).toArray(Quest[]::new);
        if(quests.length != 0) {
//            MinecraftClient.getInstance().openScreen(new SocialScreen(this));
            MinecraftClient.getInstance().openScreen(new BaseScreen(new FamiliarsScreen(this)));
        }
        return ActionResult.SUCCESS;
    }

    private void setupHair() {
        this.hairStyle = familiarsAspects.getHairStyle();
        this.hairColor = familiarsAspects.getHairColor();
    }

    private void setupEyes() {
        this.eyeColor = familiarsAspects.getEyeColor();
    }

    private void setupSkin() {
        this.skinColor = familiarsAspects.getSkinColor();
    }

    private void setupGender() {
        this.gender = familiarsGender.getGender();
    }

    private void setupProfession() {
        this.profession = familiarsProfession.getProfession();
    }

    private void setupOrientation() {
        this.sexuality = familiarsAspects.getSexuality();
    }

    public boolean canImmediatelyDespawn(double double_1) {
        return false;
    }

    public FamiliarsProfession getFamiliarsProfession() {
        return familiarsProfession;
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte byte_1) {
        if (byte_1 == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (byte_1 == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (byte_1 == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (byte_1 == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(byte_1);
        }
    }

    @Environment(EnvType.CLIENT)
    private void produceParticles(ParticleEffect particleParameters_1) {
        for (int int_1 = 0; int_1 < 5; ++int_1) {
            double double_1 = this.random.nextGaussian() * 0.02D;
            double double_2 = this.random.nextGaussian() * 0.02D;
            double double_3 = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(particleParameters_1, this.getX() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getY() + 1.0D + (double) (this.random.nextFloat() * this.getHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), double_1, double_2, double_3);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("hair_color", hairColor);
        tag.putString("eye_color", eyeColor);
        tag.putString("skin_color", skinColor);
        tag.putString("sexuality", sexuality);
        tag.putInt("hair_style", hairStyle);
        tag.putInt("friendliness", friendliness);
        tag.putInt("bravery", bravery);
        tag.putInt("generosity", generosity);
        tag.putBoolean("apologized", apologized);
        tag.putBoolean("charmed", charmed);
        tag.putString("first_name", firstName);
        tag.putString("last_name", lastName);
        tag.putInt("age", this.getBreedingAge());
        tag.putString("gender", gender);
        tag.putString("profession", profession.toString());
        tag.putBoolean("working", working);
        tag.putBoolean("sleeping", sleeping);
        if (opinions.keySet().size() > 13) {
            for (UUID key : opinions.keySet()) {
                CompoundTag opinionTag = new CompoundTag();
                opinionTag.putUuid("holder", key);
                opinionTag.putInt("opinion", opinions.get(key));
                tag.put(key.toString(), opinionTag);
            }
        }
        return tag;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }

    private void unifiedSetup() {
        this.setupGender();
        this.setupSkin();
        this.setupEyes();
        this.setupHair();
        this.setupOrientation();
        this.setupProfession();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.dataTracker.set(serverUUID, this.getUuidAsString());
        this.hairColor = tag.getString("hair_color");
        this.eyeColor = tag.getString("eye_color");
        this.skinColor = tag.getString("skin_color");
        this.hairStyle = tag.getInt("hair_style");
        this.sexuality = tag.getString("sexuality");
        this.gender = tag.getString("gender");
        this.profession = Identifier.tryParse(tag.getString("profession"));
        this.friendliness = tag.getInt("friendliness");
        this.bravery = tag.getInt("bravery");
        this.generosity = tag.getInt("generosity");
        this.apologized = tag.getBoolean("apologized");
        this.charmed = tag.getBoolean("charmed");
        this.firstName = tag.getString("first_name");
        this.lastName = tag.getString("last_name");
        this.working = tag.getBoolean("working");
        this.sleeping = tag.getBoolean("sleeping");
        for (String key : tag.getKeys()) {
            if (tag.containsUuid(key)) {
                this.opinions.put(tag.getCompound(key).getUuid("holder"), tag.getInt("opinion"));
            }
        }
        this.setBreedingAge(tag.getInt("age"));
        this.setCanPickUpLoot(true);
        this.setSpecificGoals();
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new FamiliarsEntity(Storyterium.FAMILIARS, this.world);
    }

    private String generateFirstName(String gender) throws IOException {
        String firstNameOut;
        Random rand = new Random();
        Identifier male = new Identifier(Storyterium.MOD_ID, "names/male.txt");
        Identifier female = new Identifier(Storyterium.MOD_ID, "names/female.txt");
        InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(male).getInputStream();
        InputStream stream3 = MinecraftClient.getInstance().getResourceManager().getResource(female).getInputStream();
        if (gender.equals("Male")) {
            Scanner scanner = new Scanner(stream);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append(",");
            }
            String[] strings = builder.toString().split(",");
            firstNameOut = strings[rand.nextInt(strings.length)];
            scanner.close();
            stream.close();
        } else {
            Scanner scanner2 = new Scanner(stream3);
            StringBuilder builder2 = new StringBuilder();
            while (scanner2.hasNextLine()) {
                builder2.append(scanner2.nextLine());
                builder2.append(",");
            }
            String[] strings2 = builder2.toString().split(",");
            firstNameOut = strings2[rand.nextInt(strings2.length)];
            scanner2.close();
            stream3.close();
        }
        return firstNameOut;
    }

    private String generateLastName() throws IOException {
        String lastNameOut;
        Random rand = new Random();
        Identifier surnames = new Identifier(Storyterium.MOD_ID, "names/surnames.txt");
        InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(surnames).getInputStream();
        Scanner scanner = new Scanner(stream);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append(",");
        }
        String[] strings = builder.toString().split(",");
        lastNameOut = strings[rand.nextInt(strings.length)];
        stream.close();
        scanner.close();
        return lastNameOut;
    }

    public enum VillagerThought {
        BED(115, "red_bed.png"),
        HUNGRY(116, "food.png"),
        PICK(117, "iron_pick.png"),
        HOE(118, "iron_hoe.png"),
        AXE(119, "iron_axe.png"),
        SWORD(120, "iron_sword.png"),
        BOOKSHELF(121, "bookshelf.png"),
        PIG_FOOD(122, "pig_carrot.png"),
        SHEEP_FOOD(123, "sheep_wheat.png"),
        COW_FOOD(124, "cow_wheat.png"),
        CHICKEN_FOOD(125, "chicken_seeds.png"),
        BUCKET(126, "bucket.png"),
        SHEARS(127, "shears.png"),
        TAVERN(128, "structure_tavern.png"),
        NOTEBLOCK(129, "noteblock.png"),
        TEACHER(130, "prof_teacher.png"),
        TORCH(131, "torch.png"),
        INSOMNIA(132, "insomnia.png"),
        CROWDED(133, "crowded.png"),
        DO_NOT_USE(999, "meh.png");

        private int numVal;
        private String texture;

        VillagerThought(int val, String tex) {
            this.numVal = val;
            this.texture = tex;
        }

        public int getVal() {
            return this.numVal;
        }

        public String getTex() {
            return this.texture;
        }

        public float getScale() {
            return 1.0F;
        }
    }

}