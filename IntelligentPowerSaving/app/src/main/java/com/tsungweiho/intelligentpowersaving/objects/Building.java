package com.tsungweiho.intelligentpowersaving.objects;

/**
 * Object class to store building information
 * <p>
 * This class is used to store building information used in BuildingFragment and HomeFragment
 *
 * @author Tsung Wei Ho
 * @version 0410.2015
 * @since 1.0.0
 */
public class Building {
    private String name;
    private String detail;
    private String efficiency;
    private String consumption;
    private String imageUrl;
    private String ifFollow;

    /**
     * Building constructor
     *
     * @param name        the name of the building
     * @param detail      the introduction of the building
     * @param efficiency  the energy consumption efficiency of the building in the format of "[weeklyEff],[monthlyEff],[yearlyEff]"
     * @param consumption the hourly energy consumption of the building in the format of "[1st hour consumption], ..., [24th hour consumption]"
     * @param imageUrl    the image url resource of the building
     * @param isFollow    the boolean indicate if the building is followed by user
     */
    public Building(String name, String detail, String efficiency, String consumption, String imageUrl, String isFollow) {
        this.name = name;
        this.detail = detail;
        this.efficiency = efficiency;
        this.consumption = consumption;
        this.imageUrl = imageUrl;
        this.ifFollow = isFollow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(String efficiency) {
        this.efficiency = efficiency;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIfFollow() {
        return ifFollow;
    }

    public void setIfFollow(String ifFollow) {
        this.ifFollow = ifFollow;
    }

}
