/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protokoll;

public class Head {
    private ProtokollUzenetek.Tipusok tipus;
    private ProtokollUzenetek.Feladatok feladat;

    public ProtokollUzenetek.Tipusok getTipus() {
        return tipus;
    }

    public void setTipus(ProtokollUzenetek.Tipusok tipus) {
        this.tipus = tipus;
    }

    public ProtokollUzenetek.Feladatok getFeladat() {
        return feladat;
    }

    public void setFeladat(ProtokollUzenetek.Feladatok feladat) {
        this.feladat = feladat;
    }

    public Head(ProtokollUzenetek.Tipusok tipus, ProtokollUzenetek.Feladatok feladat) {
        this.tipus = tipus;
        this.feladat = feladat;
    }

    public Head() {
    }

}
