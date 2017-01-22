package gra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MojaPlansza implements Plansza {

    private int wysokość, szerokość;
    private ArrayList<Pole> polaZPostaciami;
    private Pole[][] plansza;

    public MojaPlansza(int wysokość, int szerokość) {
        this.wysokość = wysokość;
        this.szerokość = szerokość;
        this.polaZPostaciami = new ArrayList<Pole>();
        this.plansza = new Pole[szerokość][wysokość];
    }

    private Pole znajdźPostaćNaPlanszy(Postać postać) {
        for (Pole pole : polaZPostaciami) {
            if (pole.getPostać() == postać)
                return pole;
        }
        return null;
    }

    private boolean wyjdzieZaPlanszę(Pole pole) {
        return pole.getX() < 0 || pole.getxKon() >= szerokość || pole.getY() < 0 || pole.getyKon() >= wysokość;
    }

    private boolean wyjdzieZaPlanszę(Pole pole, Kierunek kierunek) {
        return wyjdzieZaPlanszę(pole) ||
                (kierunek == Kierunek.GÓRA && pole.getY() == 0) ||
                (kierunek == Kierunek.DÓŁ && pole.getyKon() == wysokość - 1) ||
                (kierunek == Kierunek.LEWO && pole.getX() == 0) ||
                (kierunek == Kierunek.PRAWO && pole.getxKon() == szerokość - 1);
    }

    private void dodajPole(Pole pole) {
        polaZPostaciami.add(pole);
        zajmijPlansze(pole, 0, 0);
        pole.setNaPlanszy(true);
    }

    private void zajmijPlansze(Pole pole, int xPrzes, int yPrzes) {
        for (int i = pole.getX() + xPrzes; i <= pole.getxKon() + xPrzes; i++)
            for (int j = pole.getY() + yPrzes; j <= pole.getyKon() + yPrzes; j++)
                plansza[i][j] = pole;
    }

    private void zajmijPlansze(Pole pole, Kierunek kierunek) {
        switch (kierunek) {
            case GÓRA:
                zajmijPlansze(pole, 0, -1);
                return;
            case DÓŁ:
                zajmijPlansze(pole, 0, 1);
                return;
            case LEWO:
                zajmijPlansze(pole, -1, 0);
                return;
            case PRAWO:
                zajmijPlansze(pole, 1, 0);
                return;
            default:
                return;
        }
    }

    private Pole znajdźPoleBlokujące(int x0, int x1, int y0, int y1) {
        for(int x = x0; x <= x1; x++)
            for(int y = y0; y <= y1; y++)
                if(plansza[x][y] != null)
                    return plansza[x][y];
        return null;
    }

    private Pole znajdźPoleBlokujące(Pole pole) {
        return znajdźPoleBlokujące(pole.getX(), pole.getxKon(), pole.getY(), pole.getyKon());
    }

    private Pole znajdźPoleBlokujące(Pole pole, Kierunek kierunek) {
        switch (kierunek) {
            case GÓRA:
                return znajdźPoleBlokujące(pole.getX(), pole.getxKon(), pole.getY()-1, pole.getY()-1);
            case DÓŁ:
                return znajdźPoleBlokujące(pole.getX(), pole.getxKon(), pole.getyKon()+1, pole.getyKon()+1);
            case LEWO:
                return znajdźPoleBlokujące(pole.getX()-1, pole.getX()-1, pole.getY(), pole.getyKon());
            case PRAWO:
                return znajdźPoleBlokujące(pole.getxKon()+1, pole.getxKon()+1, pole.getY(), pole.getyKon());
            default:
                return null;
        }
    }

    private void odblokujPlansze(int x, int x1, int y, int y1) {
        for (int i = x; i <= x1; i++)
            for (int j = y; j <= y1; j++)
                plansza[i][j] = null;
    }

    private void odblokujZwolnione(Pole pole, Kierunek kierunek) {
        switch (kierunek) {
            case GÓRA:
                odblokujPlansze(pole.getX(), pole.getxKon(), pole.getyKon() + 1, pole.getyKon() + 1);
                return;
            case DÓŁ:
                odblokujPlansze(pole.getX(), pole.getxKon(), pole.getY() - 1, pole.getY() - 1);
                return;
            case LEWO:
                odblokujPlansze(pole.getxKon() + 1, pole.getxKon() + 1, pole.getY(), pole.getyKon());
                return;
            case PRAWO:
                odblokujPlansze(pole.getX() - 1, pole.getX() - 1, pole.getY(), pole.getyKon());
                return;
            default:
                return;
        }
    }

    private Set<Pole> znajdźBlokujących(int x0, int x1, int y0, int y1) {
        Set<Pole> blokujące = new HashSet<Pole>();
        for(int x = x0; x <= x1; x++)
            for(int y = y0; y <= y1; y++)
                if(plansza[x][y] != null)
                    blokujące.add(plansza[x][y]);
        return blokujące;
    }

    private Set<Pole> znajdźBlokujących(Pole pole, Kierunek kierunek) {
        switch (kierunek) {
            case GÓRA:
                return znajdźBlokujących(pole.getX(), pole.getxKon(), pole.getY() - 1, pole.getY() - 1);
            case DÓŁ:
                return znajdźBlokujących(pole.getX(), pole.getxKon(), pole.getyKon() + 1, pole.getyKon() + 1);
            case LEWO:
                return znajdźBlokujących(pole.getX() - 1, pole.getX() - 1, pole.getY(), pole.getyKon());
            case PRAWO:
                return znajdźBlokujących(pole.getxKon() + 1, pole.getxKon() + 1, pole.getY(), pole.getyKon());
            default:
                return null;
        }
    }

    private void szukajDeadlocka(Pole pole) throws DeadlockException {
        synchronized (this) {
            Set<Pole> odwiedzone = new HashSet<Pole>();
            Stack<Pole> stos = new Stack<Pole>();
            stos.add(pole);
            while (!stos.isEmpty()) {
                Pole topPole = stos.pop();
                if (topPole.getRuch() == null)
                    continue;
                for (Pole nastepne : znajdźBlokujących(topPole, topPole.getRuch())) {
                    if (nastepne == pole) // mam cykl
                        throw new DeadlockException();
                    if (!odwiedzone.contains(nastepne)) {
                        odwiedzone.add(nastepne);
                        stos.push(nastepne);
                    }
                }
            }
        }
    }

    private void usunPole(Pole pole) {
        polaZPostaciami.remove(pole);
        for (int i = pole.getX(); i <= pole.getxKon(); i++) {
            for (int j = pole.getY(); j <= pole.getyKon(); j++)
                plansza[i][j] = null;
        }
        pole.setNaPlanszy(false);
    }

    private boolean czyNieBlokuje(Pole pole, int x, int x1, int y, int y1) {
        return !pole.getNaPlanszy() ||
                pole.getX() > x1 || pole.getxKon() < x ||
                pole.getY() > y1 || pole.getyKon() < y;
    }

    private void czekajNaBlokujacym(Pole pole, Pole blokujący) throws InterruptedException {
        synchronized (blokujący) {
            if (pole.getRuch() == null) {
                if (czyNieBlokuje(blokujący, pole.getX(), pole.getxKon(), pole.getY(), pole.getyKon()))
                    return;
            }
            else {
                    if((pole.getRuch() == Kierunek.GÓRA && czyNieBlokuje(blokujący, pole.getX(), pole.getxKon(), pole.getY() - 1, pole.getY() - 1)) ||
                    (pole.getRuch() == Kierunek.DÓŁ && czyNieBlokuje(blokujący, pole.getX(), pole.getxKon(), pole.getyKon() + 1, pole.getyKon() + 1)) ||
                    (pole.getRuch() == Kierunek.LEWO && czyNieBlokuje(blokujący, pole.getX() - 1, pole.getX() - 1, pole.getY(), pole.getyKon())) ||
                    (pole.getRuch() == Kierunek.PRAWO && czyNieBlokuje(blokujący, pole.getxKon() + 1, pole.getxKon() + 1, pole.getY(), pole.getyKon())))
                        return;
            }
            blokujący.wait();
        }
    }

    public void postaw(Postać postać, int wiersz, int kolumna) throws InterruptedException {
        synchronized (postać) {
            Pole stawiane, poleBlokujące;
            synchronized (this) { // pilnuje od razu dostępu do planszy
                stawiane = new Pole(postać, kolumna, wiersz);
                if (znajdźPostaćNaPlanszy(postać) != null || wyjdzieZaPlanszę(stawiane))
                    throw new InterruptedException("Błędne postawienie, postać już jest na planszy lub stawiasz ją poza planszą!");
            }
            while (true) {
                synchronized (this) {
                    poleBlokujące = znajdźPoleBlokujące(stawiane);
                    if (poleBlokujące == null) {
                        dodajPole(stawiane);
                        return;
                    }
                }
                czekajNaBlokujacym(stawiane, poleBlokujące);
            }
        }
    }

    public void przesuń(Postać postać, Kierunek kierunek) throws InterruptedException, DeadlockException {
        synchronized (postać) {

            Pole przesuwane, poleBlokujące;
            synchronized (this) {
                przesuwane = znajdźPostaćNaPlanszy(postać);
                if (przesuwane == null)
                    throw new InterruptedException("Błędne przesunięcie, postaci nie ma na planszy!");
                if (wyjdzieZaPlanszę(przesuwane, kierunek))
                    throw new InterruptedException("Błędne przesunięcie, postać wypadnie poza planszę w wyniku ruchu!");
            }
            while (true) {
                synchronized (this) {
                    przesuwane.setRuch(kierunek);
                    poleBlokujące = znajdźPoleBlokujące(przesuwane, kierunek);
                    if (poleBlokujące == null) {
                        synchronized (przesuwane) {
                            zajmijPlansze(przesuwane, kierunek);
                            przesuwane.przesuń(kierunek);
                            odblokujZwolnione(przesuwane, kierunek);
                            przesuwane.setRuch(null);
                            przesuwane.notifyAll();
                            return;
                        }
                    }
                    try {
                        szukajDeadlocka(przesuwane);
                    } catch (DeadlockException e) {
                        przesuwane.setRuch(null);
                        throw e;
                    }
                }
                czekajNaBlokujacym(przesuwane, poleBlokujące);
            }
        }
    }

    public void usuń(Postać postać) {
        synchronized (postać) {
            synchronized (this) {
                Pole pole = znajdźPostaćNaPlanszy(postać);
                if (pole == null)
                    throw new IllegalArgumentException("Postaci nie ma na planszy!");
                synchronized (pole) {
                    usunPole(pole);
                    pole.notifyAll();
                }
            }
        }
    }

    public void sprawdź(int wiersz, int kolumna, Akcja jeśliZajęte, Runnable jeśliWolne) {
        synchronized (this) {
            if (wiersz < 0 || wiersz >= wysokość || kolumna < 0 || kolumna >= szerokość)
                throw new IllegalArgumentException("Podane pole jest poza planszą!");
            if (plansza[kolumna][wiersz] != null) {
                Postać postać = plansza[kolumna][wiersz].getPostać();
                jeśliZajęte.wykonaj(postać);
            } else {
                jeśliWolne.run();
            }
        }
    }

    public synchronized void drukujPlanszę() {
        for (int y = 0; y < wysokość; y++) {
            for (int x = 0; x < szerokość; x++) {
                if (plansza[x][y] != null)
                    System.out.print("x");
                else
                    System.out.print(".");
            }
            System.out.println();
        }
        System.out.println();
    }
}