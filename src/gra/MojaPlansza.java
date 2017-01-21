package gra;

import java.util.ArrayList;

public class MojaPlansza implements Plansza {

    private int wysokość, szerokość;
    private ArrayList<Pole> polaZPostaciami;
    private Pole[][] plansza;

    public MojaPlansza(int wysokość, int szerokość) {
        this.wysokość = wysokość;
        this.szerokość = szerokość;
        this.polaZPostaciami = new ArrayList<Pole>();
        this.plansza = new Pole[szerokość][wysokość];
        for(int i=0; i<szerokość; i++)
            for(int j=0; j<wysokość; j++)
                plansza[i][j] = new Pole();
    }

    Pole znajdźPostaćNaPlanszy(Postać postać) {
        for(Pole pole : polaZPostaciami) {
            if(pole.getPostać() == postać)
                return pole;
        }
        return null;
    }

    boolean wyjdzieZaPlanszę(Pole pole) {
        return pole.getX() < 0 || pole.getxKon() >= szerokość || pole.getY() < 0 || pole.getyKon() >= wysokość;
    }

    void dodajPole(Pole pole) {
        polaZPostaciami.add(pole);
        // TODO: zajmij pola
        pole.setNaPlanszy(true);
    }

    Pole znajdźPoleBlokujące(Pole pole) {
        for(int i=pole.getX(); i<=pole.getxKon(); i++) {
            for(int j=pole.getY(); j<=pole.getyKon(); j++) {
                if(plansza[i][j] != null)
                    return plansza[i][j];
            }
        }
        return null;
    }

    public void postaw(Postać postać, int wiersz, int kolumna) throws InterruptedException {
        synchronized(postać) {
            Pole stawiane = new Pole(postać, kolumna, wiersz);
            synchronized(this) { // pilnuje od razu dostępu do planszy
                if(znajdźPostaćNaPlanszy(postać) != null || wyjdzieZaPlanszę(stawiane))
                    throw new InterruptedException("Błędne postawienie, postać już jest na planszy lub z niej wypadnie!");
            }
            while(true) {
                Pole poleBlokujące = null;
                synchronized(this) {
                    poleBlokujące = znajdźPoleBlokujące(stawiane);
                    if(poleBlokujące == null) {
                       dodajPole(stawiane);
                        break;
                    }
                }
                if(poleBlokujące != null)
                    poleBlokujące.wait();
            }
        }
    }

    public void przesuń(Postać postać, Kierunek kierunek) throws InterruptedException, DeadlockException {

    }

    public void usuń(Postać postać) {

    }

    public void sprawdź(int wiersz, int kolumna, Akcja jeśliZajęte, Runnable jeśliWolne) {

    }

    public static void main(String[] args) {
        System.out.print("HALO");
    }

}
