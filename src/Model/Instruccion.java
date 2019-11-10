/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author jpmazate
 */
public class Instruccion {
    
    private String tipoMovimiento;
    private int tiempo;
    private String velocidad;

    public Instruccion(String tipoMovimiento, int tiempo, String velocidad) {
        this.tipoMovimiento = tipoMovimiento;
        this.tiempo = tiempo;
        this.velocidad = velocidad;
    }

    
    
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }
    
    
    
}
