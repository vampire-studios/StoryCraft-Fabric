package io.github.vampirestudios.storyterium.socialVillager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.storyterium.main.Storyterium;
import net.minecraft.util.Identifier;

public class Professions {

    protected static Codec<Professions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Identifier.CODEC.fieldOf("name").forGetter((professions) -> professions.name)
    ).apply(instance, Professions::new));

    public static final Professions LUMBERJACK = new Professions(Storyterium.id("lumerjack"));
    public static final Professions FARMER = new Professions(Storyterium.id("farmer"));
    public static final Professions ARCHITECT = new Professions(Storyterium.id("architect"));
    public static final Professions BLACKSMITH = new Professions(Storyterium.id("blacksmith"));
    public static final Professions ENCHANTER = new Professions(Storyterium.id("enchanter"));
    public static final Professions DRUID = new Professions(Storyterium.id("druid"));
    public static final Professions BUTCHER = new Professions(Storyterium.id("butcher"));
    public static final Professions LIBRARIAN = new Professions(Storyterium.id("librarian"));
    public static final Professions NOMAD = new Professions(Storyterium.id("nomad"));
    public static final Professions BAKER = new Professions(Storyterium.id("baker"));
    public static final Professions PRIEST = new Professions(Storyterium.id("priest"));
    public static final Professions MINER = new Professions(Storyterium.id("miner"));
    public static final Professions GUARD = new Professions(Storyterium.id("guard"));
    public static final Professions ARCHER = new Professions(Storyterium.id("archer"));

    public Identifier name;

    public Professions(Identifier name) {
        this.name = name;
    }

    public Identifier getName() {
        return name;
    }

}