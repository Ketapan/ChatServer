package Prozess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

public class UnzipMessage {

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    String messageTo = "";


    public ArrayList unzip(byte[] pDaten) throws IOException
    {
        InputStream input = new ByteArrayInputStream(pDaten);
        byte[] daten = new byte[2048];
        ZipInputStream zip = new ZipInputStream(input);
        int anzahl = 0;
        ArrayList<String> ergebnisListe = new ArrayList<>();

        while((zip.getNextEntry()) != null)
        {
            anzahl = zip.read(daten);
            byte[] bla = new byte[anzahl];
            System.arraycopy(daten, 0, bla, 0, anzahl);
            ergebnisListe.add(new String(bla));
        }

        zip.close();
        input.close();
//
//        byte[] datenKorrekt = new byte[anzahl];
//        System.arraycopy( daten, 0, datenKorrekt, 0, anzahl );

        return ergebnisListe;
    }
}
