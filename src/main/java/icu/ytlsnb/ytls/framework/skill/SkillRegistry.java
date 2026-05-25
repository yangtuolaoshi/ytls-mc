package icu.ytlsnb.ytls.framework.skill;

import icu.ytlsnb.ytls.framework.skill.annotation.RegisterSkill;
import icu.ytlsnb.ytls.framework.skill.api.Skill;
import icu.ytlsnb.ytls.framework.util.ClasspathScanner;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SkillRegistry {
    private static final Logger LOG = FrameworkLog.of("skill-registry");
    private static final Map<ResourceLocation, Skill> skills = new HashMap<>();

    private SkillRegistry() {
    }

    public static void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClasspathScanner.scanByAnnotation(basePackage, RegisterSkill.class)) {
            if (!Skill.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException("@RegisterSkill must implement Skill: " + clazz.getName());
            }
            RegisterSkill meta = clazz.getAnnotation(RegisterSkill.class);
            register(instantiate(clazz), meta.value());
        }
    }

    public static void register(Skill skill, String id) {
        ResourceLocation location = ModResources.loc(id);
        if (skills.containsKey(location)) {
            throw new IllegalStateException("Duplicate skill id: " + id);
        }
        skills.put(location, skill);
        LOG.info("Registered skill: {}", location);
    }

    public static Optional<Skill> find(ResourceLocation id) {
        return Optional.ofNullable(skills.get(id));
    }

    public static Skill require(ResourceLocation id) {
        return find(id).orElseThrow(() -> new IllegalStateException("Skill not found: " + id));
    }

    public static Skill require(String id) {
        return require(ModResources.loc(id));
    }

    public static Map<ResourceLocation, Skill> all() {
        return Map.copyOf(skills);
    }

    private static Skill instantiate(Class<?> clazz) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (Skill) ctor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate skill: " + clazz.getName(), ex);
        }
    }
}
