package com.example.smart.test1.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by Smart on 2017-12-19.
 */

public class ProvinceBean implements IPickerViewData {
    private long id;
    private String name;
    private String description;
    private String others;

    public ProvinceBean(long id,String name,String description,String others){
        this.id = id;
        this.name = name;
        this.description = description;
        this.others = others;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getPickerViewText() {

        return name;
    }
}
