package com.ns.empaque.wmpempaque.Desgrane;

/**
 * Created by jcalderon on 28/09/2016.
 */
public class BoxesFolioInLine {

    private int idProduct, idProductLog, lineNumber, farmNumber, boxesInLine, boxesAssignToPallet, BoxesAvailable, totalBoxes, casesGenerados;
    private String vFolio, lineName,fechaEnterLine, farmName, SKU, GH, cajasDesgranadas;
    private double lbsXBox, lbsPorSKU;
    private boolean estado;

    /****************************************************/
    public String getCajasDesgranadas() {
        return cajasDesgranadas;
    }

    public void setCajasDesgranadas(String cajasDesgranadas) {
        this.cajasDesgranadas = cajasDesgranadas;
    }

    public double getLbsPorSKU() {
        return lbsPorSKU;
    }

    public void setLbsPorSKU(double lbsPorSKU) {
        this.lbsPorSKU = lbsPorSKU;
    }

    public boolean isEstado() {
        return estado;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getCasesGenerados() {
        return casesGenerados;
    }

    public void setCasesGenerados(int casesGenerados) {
        this.casesGenerados = casesGenerados;
    }

    /****************************************************/



    public int getIdProductLog() {
        return idProductLog;
    }

    public void setIdProductLog(int idProductLog) {
        this.idProductLog = idProductLog;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getTotalBoxes() {
        return totalBoxes;
    }

    public void setTotalBoxes(int totalBoxes) {
        this.totalBoxes = totalBoxes;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getGH() {
        return GH;
    }

    public void setGH(String GH) {
        this.GH = GH;
    }

    public double getLbsXBox() {
        return lbsXBox;
    }

    public void setLbsXBox(double lbsXBox) {
        this.lbsXBox = lbsXBox;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getFarmNumber() {
        return farmNumber;
    }

    public void setFarmNumber(int farmNumber) {
        this.farmNumber = farmNumber;
    }

    public int getBoxesInLine() {
        return boxesInLine;
    }

    public void setBoxesInLine(int boxesInLine) {
        this.boxesInLine = boxesInLine;
    }

    public int getBoxesAssignToPallet() {
        return boxesAssignToPallet;
    }

    public void setBoxesAssignToPallet(int boxesAssignToPallet) {
        this.boxesAssignToPallet = boxesAssignToPallet;
    }

    public int getBoxesAvailable() {
        return BoxesAvailable;
    }

    public void setBoxesAvailable(int boxesAvailable) {
        BoxesAvailable = boxesAvailable;
    }

    public String getvFolio() {
        return vFolio;
    }

    public void setvFolio(String vFolio) {
        this.vFolio = vFolio;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getFechaEnterLine() {
        return fechaEnterLine;
    }

    public void setFechaEnterLine(String fechaEnterLine) {
        this.fechaEnterLine = fechaEnterLine;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }
}
