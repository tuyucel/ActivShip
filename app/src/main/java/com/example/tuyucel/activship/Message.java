package com.example.tuyucel.activship;

public class Message {
    /*Mesaj yapısını tutabileceğimiz bir model oluşturuyoruz bu model de
    json yapısına göre tasarlanması gerekiyor.*/

    String mesajText,gonderici,zaman;

    public Message(){

    }

    public Message(String mesajText, String gonderici, String zaman){
        this.mesajText = mesajText;
        this.gonderici = gonderici;
        this.zaman = zaman;

    }
    public String getMesajText() {
        return mesajText;
    }

    public String getGonderici() {
        return gonderici;
    }

    public String getZaman() {
        return zaman;
    }

    public void setMesajText(String mesajText) {
        this.mesajText = mesajText;
    }

    public void setGonderici(String gonderici) {
        this.gonderici = gonderici;
    }

    public void setZaman(String zaman) {
        this.zaman = zaman;
    }


}
