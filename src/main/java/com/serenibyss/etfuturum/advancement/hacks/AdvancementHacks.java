package com.serenibyss.etfuturum.advancement.hacks;

import com.google.gson.JsonObject;
import com.serenibyss.etfuturum.EtFuturum;
import com.serenibyss.etfuturum.load.feature.Feature;
import com.serenibyss.etfuturum.mixin.core.AdvancementBuilderAccessor;
import net.minecraft.advancements.*;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;

public class AdvancementHacks {

    public static void addKilledTrigger(Feature feature, String advancementName,
                                         Map<ResourceLocation, Advancement.Builder> map, String entityId) {
        Advancement.Builder builder = getBuilder(feature, advancementName, map);
        if (builder == null) {
            return;
        }

        ResourceLocation entityResLoc = new ResourceLocation(entityId);
        JsonObject root = new JsonObject();
        root.addProperty("trigger", "minecraft:player_killed_entity");
        JsonObject conditions = new JsonObject();
        JsonObject entityObj = new JsonObject();
        entityObj.addProperty("type", entityResLoc.toString());
        conditions.add("entity", entityObj);
        root.add("conditions", conditions);

        String criterionName = entityResLoc.getPath();
        Advancement.Builder newBuilder = addCriterion(builder, criterionName, root);
        if (newBuilder != null) {
            map.put(new ResourceLocation(advancementName), newBuilder);
        }
    }

    public static void addBredTrigger(Feature feature, String advancementName,
                                      Map<ResourceLocation, Advancement.Builder> map, String entityId) {
        Advancement.Builder builder = getBuilder(feature, advancementName, map);
        if (builder == null) {
            return;
        }

        ResourceLocation entityResLoc = new ResourceLocation(entityId);
        JsonObject root = new JsonObject();
        root.addProperty("trigger", "minecraft:bred_animals");
        JsonObject conditions = new JsonObject();
        JsonObject entityObj = new JsonObject();
        entityObj.addProperty("type", entityResLoc.toString());
        conditions.add("parent", entityObj);
        root.add("conditions", conditions);

        String criterionName = "bred_" + entityResLoc.getPath();
        Advancement.Builder newBuilder = addCriterion(builder, criterionName, root);
        if (newBuilder != null) {
            map.put(new ResourceLocation(advancementName), builder);
        }
    }

    private static Advancement.Builder addCriterion(Advancement.Builder builder, String name, JsonObject criterionJson) {
        Criterion criterion = Criterion.criterionFromJson(criterionJson, null);
        Map<String, Criterion> criterionMap = ((AdvancementBuilderAccessor) builder).getCriteria();
        criterionMap.put(name, criterion);

        String[][] reqs = ((AdvancementBuilderAccessor) builder).getRequirements();
        if (reqs.length == 1) {
            // insert into the single array
            String[] requirements = reqs[0];
            String[] newRequirements = new String[requirements.length + 1];
            System.arraycopy(requirements, 0, newRequirements, 0, requirements.length);
            newRequirements[requirements.length] = name;
            reqs[0] = newRequirements;
            return null;
        } else {
            // insert into a new array
            String[][] newRequirements = new String[reqs.length + 1][];
            System.arraycopy(reqs, 0, newRequirements, 0, reqs.length);
            newRequirements[reqs.length] = new String[]{name};
            // rebuild the array since we expanded the outer array, which is final on the builder
            return rebuild(name, builder, null, null, null, newRequirements);
        }
    }

    private static Advancement.Builder getBuilder(Feature feature, String advancementName,
                                                  Map<ResourceLocation, Advancement.Builder> map) {
        if (!feature.isEnabled()) {
            return null;
        }
        return map.get(new ResourceLocation(advancementName));
    }

    private static Advancement.Builder rebuild(String name,
                                               Advancement.Builder b,
                                               @Nullable DisplayInfo display,
                                               @Nullable AdvancementRewards rewards,
                                               @Nullable Map<String, Criterion> criteria,
                                               @Nullable String[][] requirements) {
        ResourceLocation parentId = ((AdvancementBuilderAccessor) b).getParentId();
        Advancement parent = ((AdvancementBuilderAccessor) b).getParent();
        if (display == null) display = ((AdvancementBuilderAccessor) b).getDisplay();
        if (rewards == null) rewards = ((AdvancementBuilderAccessor) b).getRewards();
        if (criteria == null) criteria = ((AdvancementBuilderAccessor) b).getCriteria();
        if (requirements == null) requirements = ((AdvancementBuilderAccessor) b).getRequirements();

        try {
            Constructor<Advancement.Builder> ctor = Advancement.Builder.class.getDeclaredConstructor(ResourceLocation.class, DisplayInfo.class, AdvancementRewards.class, Map.class, String[][].class);
            ctor.setAccessible(true);
            Advancement.Builder newBuilder = ctor.newInstance(parentId, display, rewards, criteria, requirements);
            ((AdvancementBuilderAccessor) newBuilder).setParent(parent);
            return newBuilder;
        } catch (ReflectiveOperationException e) {
            EtFuturum.LOGGER.error("Failed to edit Minecraft advancement '{}', skipping...", name);
        }
        return null;
    }
}
