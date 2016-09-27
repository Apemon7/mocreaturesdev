package drzhark.mocreatures.entity.passive;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.ai.EntityAIFleeFromPlayer;
import drzhark.mocreatures.entity.ai.EntityAIFollowOwnerPlayer;
import drzhark.mocreatures.entity.ai.EntityAIHunt;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import drzhark.mocreatures.util.MoCSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class MoCEntityPetScorpion extends MoCEntityTameableAnimal {

    public static final String scorpionNames[] = {"Dirt", "Cave", "Nether", "Frost", "Undead"};
    private boolean isPoisoning;
    private int poisontimer;
    public int mouthCounter;
    public int armCounter;
    private int transformCounter;
    private static final DataParameter<Boolean> RIDEABLE = EntityDataManager.<Boolean>createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_BABIES = EntityDataManager.<Boolean>createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SITTING = EntityDataManager.<Boolean>createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    

    public MoCEntityPetScorpion(World world) {
        super(world);
        setSize(1.4F, 0.9F);
        this.poisontimer = 0;
        setAdult(true);//TODO
        setEdad(20);
        setHasBabies(false);
        this.stepHeight = 20.0F;
    }

    @Override
    protected void initEntityAI() {
    	this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(4, new EntityAIWanderMoC2(this, 1.0D));
        this.tasks.addTask(5, new EntityAIFleeFromPlayer(this, 1.2D, 4D));
        this.tasks.addTask(6, new EntityAIFollowOwnerPlayer(this, 1.0D, 2F, 10F));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHunt(this, EntityAnimal.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    @Override
    public void selectType() {
        if (getType() == 0) {
            setType(1);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        boolean saddle = getIsRideable();

        if (this.transformCounter != 0) {
            String newText = saddle ? "scorpionundeadsaddle.png" : "scorpionundead.png";
            if ((this.transformCounter % 5) == 0) {
                return MoCreatures.proxy.getTexture(newText);
            }
            if (this.transformCounter > 50 && (this.transformCounter % 3) == 0) {
                return MoCreatures.proxy.getTexture(newText);
            }

            if (this.transformCounter > 75 && (this.transformCounter % 4) == 0) {
                return MoCreatures.proxy.getTexture(newText);
            }
        }

        switch (getType()) {
            case 1:
                if (!saddle) {
                    return MoCreatures.proxy.getTexture("scorpiondirt.png");
                }
                return MoCreatures.proxy.getTexture("scorpiondirtsaddle.png");
            case 2:
                if (!saddle) {
                    return MoCreatures.proxy.getTexture("scorpioncave.png");
                }
                return MoCreatures.proxy.getTexture("scorpioncavesaddle.png");
            case 3:
                if (!saddle) {
                    return MoCreatures.proxy.getTexture("scorpionnether.png");
                }
                return MoCreatures.proxy.getTexture("scorpionnethersaddle.png");
            case 4:
                if (!saddle) {
                    return MoCreatures.proxy.getTexture("scorpionfrost.png");
                }
                return MoCreatures.proxy.getTexture("scorpionfrostsaddle.png");
            case 5:
                if (!saddle) {
                    return MoCreatures.proxy.getTexture("scorpionundead.png");
                }
                return MoCreatures.proxy.getTexture("scorpionundeadsaddle.png");
            default:
                return MoCreatures.proxy.getTexture("scorpiondirt.png");
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HAS_BABIES, Boolean.valueOf(false));
        this.dataManager.register(IS_SITTING, Boolean.valueOf(false));
        this.dataManager.register(RIDEABLE, Boolean.valueOf(false));
    }
    
    @Override
    public void setRideable(boolean flag) {
    	this.dataManager.set(RIDEABLE, Boolean.valueOf(flag));
    }

    @Override
    public boolean getIsRideable() {
    	return ((Boolean)this.dataManager.get(RIDEABLE)).booleanValue();
    }
    
    public boolean getHasBabies() {
        return getIsAdult() && ((Boolean)this.dataManager.get(HAS_BABIES)).booleanValue();
    }

    public boolean getIsPicked() {
        return this.getRidingEntity() != null;
    }

    public boolean getIsPoisoning() {
        return this.isPoisoning;
    }

    @Override
    public boolean getIsSitting() {
    	return ((Boolean)this.dataManager.get(IS_SITTING)).booleanValue();
    }

    public void setSitting(boolean flag) {
    	this.dataManager.set(IS_SITTING, Boolean.valueOf(flag));
    }

    public void setHasBabies(boolean flag) {
    	this.dataManager.set(HAS_BABIES, Boolean.valueOf(flag));
    }

    public void setPoisoning(boolean flag) {
        if (flag && MoCreatures.isServer()) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0),
                    new TargetPoint(this.worldObj.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        this.isPoisoning = flag;
    }

    @Override
    public void performAnimation(int animationType) {
        if (animationType == 0) //tail animation
        {
            setPoisoning(true);
        } else if (animationType == 1) //arm swinging
        {
            this.armCounter = 1;
            //swingArm();
        } else if (animationType == 3) //movement of mouth
        {
            this.mouthCounter = 1;
        } else if (animationType == 5) //transforming into undead scorpion
        {
            this.transformCounter = 1;
        }
    }

    @Override
    public boolean isOnLadder() {
        return this.isCollidedHorizontally;
    }

    public boolean climbing() {
        return !this.onGround && isOnLadder();
    }

    @Override
    public void onLivingUpdate() {
        if (!this.onGround && (this.getRidingEntity() != null)) {
            this.rotationYaw = this.getRidingEntity().rotationYaw;
        }

        if (this.mouthCounter != 0 && this.mouthCounter++ > 50) {
            this.mouthCounter = 0;
        }

        if (MoCreatures.isServer() && (this.armCounter == 10 || this.armCounter == 40)) {
            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_CLAW);
        }

        if (this.armCounter != 0 && this.armCounter++ > 24) {
            this.armCounter = 0;
        }

        if (getIsPoisoning()) {
            this.poisontimer++;
            if (this.poisontimer == 1) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_STING);
            }
            if (this.poisontimer > 50) {
                this.poisontimer = 0;
                setPoisoning(false);
            }
        }

        if (this.transformCounter > 0) {
            if (this.transformCounter == 40) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_TRANSFORM);
            }

            if (++this.transformCounter > 100) {
                this.transformCounter = 0;
                setType(5);
                selectType();
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (super.attackEntityFrom(damagesource, i)) {
            Entity entity = damagesource.getEntity();
            if (!(entity instanceof EntityLivingBase) || ((entity != null) && (entity instanceof EntityPlayer) && getIsTamed())) {
                return false;
            }

            if ((entity != null) && (entity != this) && (super.shouldAttackPlayers()) && getIsAdult()) {
                setAttackTarget((EntityLivingBase) entity);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
        boolean flag = (entityIn instanceof EntityPlayer);
        if (!getIsPoisoning() && this.rand.nextInt(5) == 0 && entityIn instanceof EntityLivingBase) {
            setPoisoning(true);
            if (getType() <= 2)// regular scorpions
            {
                if (flag) {
                    MoCreatures.poisonPlayer((EntityPlayer) entityIn);
                }
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 70, 0));
            } else if (getType() == 4)// blue scorpions
            {
                if (flag) {
                    MoCreatures.freezePlayer((EntityPlayer) entityIn);
                }
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 70, 0));

            } else if (getType() == 3)// red scorpions
            {
                if (flag && MoCreatures.isServer() && !this.worldObj.provider.doesWaterVaporize()) {
                    MoCreatures.burnPlayer((EntityPlayer) entityIn);
                    ((EntityLivingBase) entityIn).setFire(15);
                }
            }
        } else {
            swingArm();
        }
        super.applyEnchantments(entityLivingBaseIn, entityIn);
    }

    public void swingArm() {
        if (MoCreatures.isServer()) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1),
                    new TargetPoint(this.worldObj.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
    }

    public boolean swingingTail() {
        return getIsPoisoning() && this.poisontimer < 15;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MoCSoundEvents.ENTITY_SCORPION_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound() {
        return MoCSoundEvents.ENTITY_SCORPION_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (MoCreatures.isServer()) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3),
                    new TargetPoint(this.worldObj.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }

        return MoCSoundEvents.ENTITY_SCORPION_AMBIENT;
    }

    @Override
    protected Item getDropItem() {
        if (!getIsAdult()) {
            return Items.STRING;
        }

        boolean flag = (this.rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);

        switch (getType()) {
            case 1:
                if (flag) {
                    return MoCreatures.scorpStingDirt;
                }
                return MoCreatures.chitin;
            case 2:
                if (flag) {
                    return MoCreatures.scorpStingCave;
                }
                return MoCreatures.chitinCave;
            case 3:
                if (flag) {
                    return MoCreatures.scorpStingNether;
                }
                return MoCreatures.chitinNether;
            case 4:
                if (flag) {
                    return MoCreatures.scorpStingFrost;
                }
                return MoCreatures.chitinFrost;
            case 5:
                return Items.ROTTEN_FLESH;

            default:
                return Items.STRING;
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
        if (super.processInteract(player, hand, stack)) {
            return false;
        }

        if ((stack != null) && getIsAdult() && !getIsRideable()
                && (stack.getItem() == Items.SADDLE || stack.getItem() == MoCreatures.horsesaddle)) {
            if (--stack.stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            setRideable(true);
            return true;
        }

        if ((stack != null) && (stack.getItem() == MoCreatures.whip) && getIsTamed() && (!this.isBeingRidden())) {
            setSitting(!getIsSitting());
            return true;
        }

        if ((stack != null) && this.getIsTamed() && stack.getItem() == MoCreatures.essenceundead) {
            if (--stack.stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
            transform(5);
            return true;
        }

        if ((stack != null) && this.getIsTamed() && stack.getItem() == MoCreatures.essencedarkness) {
            if (--stack.stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
            this.setHealth(getMaxHealth());
            if (MoCreatures.isServer()) {
                int i = getType() + 40;
                MoCEntityEgg entityegg = new MoCEntityEgg(this.worldObj, i);
                entityegg.setPosition(player.posX, player.posY, player.posZ);
                player.worldObj.spawnEntityInWorld(entityegg);
                entityegg.motionY += this.worldObj.rand.nextFloat() * 0.05F;
                entityegg.motionX += (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.3F;
                entityegg.motionZ += (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.3F;
            }
            return true;
        }
        if (this.getRidingEntity() == null && getEdad() < 60 && !getIsAdult()) {
            this.rotationYaw = player.rotationYaw;
            if (MoCreatures.isServer()) {
                this.startRiding(player);
            }

            if (MoCreatures.isServer() && !getIsTamed()) {
                MoCTools.tameWithName(player, this);
            }
        } else if (this.getRidingEntity() != null && getIsPicked()) {
            MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
            if (MoCreatures.isServer()) {
                this.dismountEntity();
            }
            this.motionX = player.motionX * 5D;
            this.motionY = (player.motionY / 2D) + 0.5D;
            this.motionZ = player.motionZ * 5D;
        }

        if (getIsRideable() && getIsTamed() && getIsAdult() && (!this.isBeingRidden())) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            if (MoCreatures.isServer()) {
                player.startRiding(this);
            }

            return true;
        }

        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        setHasBabies(nbttagcompound.getBoolean("Babies"));
        setRideable(nbttagcompound.getBoolean("Saddled"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Babies", getHasBabies());
        nbttagcompound.setBoolean("Saddled", getIsRideable());
    }

    @Override
    public int nameYOffset() {
        int n = (int) (1 - (getEdad() * 0.8));
        if (n < -60) {
            n = -60;
        }
        if (getIsAdult()) {
            n = -60;
        }
        if (getIsSitting()) {
            n = (int) (n * 0.8);
        }
        return n;
    }

    @Override
    protected boolean isMyHealFood(ItemStack itemstack) {
        return (itemstack.getItem() == MoCreatures.ratRaw || itemstack.getItem() == MoCreatures.ratCooked);
    }

    @Override
    public int getTalkInterval() {
        return 300;
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isBeingRidden();
    }

    @Override
    public boolean rideableEntity() {
        return true;
    }

    @Override
    public boolean isMovementCeased() {
        return (this.isBeingRidden()) || getIsSitting();
    }

    @Override
    public void dropMyStuff() {
        MoCTools.dropSaddle(this, this.worldObj);
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public float getAdjustedYOffset() {
        return 0.2F;
    }

    @Override
    public int getMaxEdad() {
        return 120;
    }

    @Override
    public double getMountedYOffset() {
        return (this.height * 0.75D) - 0.15D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        double dist = (0.2D);
        double newPosX = this.posX + (dist * Math.sin(this.renderYawOffset / 57.29578F));
        double newPosZ = this.posZ - (dist * Math.cos(this.renderYawOffset / 57.29578F));
        passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
    }

    @Override
    public boolean isNotScared() {
        return getIsTamed();
    }

    public void transform(int tType) {
        if (MoCreatures.isServer()) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), tType),
                    new TargetPoint(this.worldObj.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        this.transformCounter = 1;
    }

    @Override
    public boolean isReadyToHunt() {
        return this.getIsAdult() && !this.isMovementCeased();
    }

    @Override
    public boolean canAttackTarget(EntityLivingBase entity) {
        return !(entity instanceof MoCEntityFox) && entity.height <= 1D && entity.width <= 1D;
    }

}
