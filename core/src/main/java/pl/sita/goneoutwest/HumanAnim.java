package pl.sita.goneoutwest;

public enum HumanAnim {

    DEFAULT,
    SHOOT_TOP,
    SHOOT_BOTTOM;



    String getTextureName() {
        switch (this) {
            case DEFAULT:
                return "hero_side.png";
            case SHOOT_TOP:
                return "hero_side_shoot_top.png";
            case SHOOT_BOTTOM:
                return "hero_side_shoot_bottom.png";
        }
        throw new RuntimeException();
    }

    String getTextureNameEnemy() {
        switch (this) {
            case DEFAULT:
                return "enemy_side.png";
            case SHOOT_TOP:
                return "enemy_side_shoot_top.png";
            case SHOOT_BOTTOM:
                return "enemy_side_shoot_bottom.png";
        }
        throw new RuntimeException();
    }


}
