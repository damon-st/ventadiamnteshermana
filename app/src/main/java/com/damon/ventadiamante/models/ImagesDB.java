package com.damon.ventadiamante.models;

import java.io.Serializable;

public class ImagesDB implements Serializable {
    String ref;
    String img;

    public ImagesDB(String ref, String img) {
        this.ref = ref;
        this.img = img;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
