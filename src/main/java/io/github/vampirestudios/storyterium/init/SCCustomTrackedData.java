package io.github.vampirestudios.storyterium.init;

import io.github.vampirestudios.storyterium.socialVillager.FamiliarsAspects;
import io.github.vampirestudios.storyterium.socialVillager.FamiliarsGender;
import io.github.vampirestudios.storyterium.socialVillager.FamiliarsProfession;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SCCustomTrackedData {

    public static final TrackedDataHandler<FamiliarsAspects> ASPECTS = new TrackedDataHandler<FamiliarsAspects>() {
        @Override
        public void write(PacketByteBuf byteBuf, FamiliarsAspects familiarsAspects) {
            byteBuf.writeString(familiarsAspects.getHairColor());
            byteBuf.writeString(familiarsAspects.getEyeColor());
            byteBuf.writeString(familiarsAspects.getSkinColor());
            byteBuf.writeString(familiarsAspects.getSexuality());
            byteBuf.writeInt(familiarsAspects.getHairStyle());
        }

        @Override
        public FamiliarsAspects read(PacketByteBuf byteBuf) {
            return new FamiliarsAspects(byteBuf.readString(), byteBuf.readString(), byteBuf.readString(), byteBuf.readString(), byteBuf.readInt());
        }

        @Override
        public FamiliarsAspects copy(FamiliarsAspects familiarsAspects) {
            return familiarsAspects;
        }

    };

    public static final TrackedDataHandler<FamiliarsGender> GENDER = new TrackedDataHandler<FamiliarsGender>() {
        @Override
        public void write(PacketByteBuf byteBuf, FamiliarsGender familiarsGender) {
            byteBuf.writeString(familiarsGender.getGender());
        }

        @Override
        public FamiliarsGender read(PacketByteBuf byteBuf) {
            return new FamiliarsGender(byteBuf.readString());
        }

        @Override
        public FamiliarsGender copy(FamiliarsGender familiarsGender) {
            return familiarsGender;
        }

    };

    public static final TrackedDataHandler<FamiliarsProfession> PROFESSION = new TrackedDataHandler<FamiliarsProfession>() {
        @Override
        public void write(PacketByteBuf byteBuf, FamiliarsProfession familiarsProfession) {
            byteBuf.writeString(familiarsProfession.getProfession().toString());
        }

        @Override
        public FamiliarsProfession read(PacketByteBuf byteBuf) {
            return new FamiliarsProfession(Identifier.tryParse(byteBuf.readString()));
        }

        @Override
        public FamiliarsProfession copy(FamiliarsProfession familiarsProfession) {
            return familiarsProfession;
        }

    };

}
