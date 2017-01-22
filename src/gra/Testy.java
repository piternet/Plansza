package gra;

import java.util.ArrayList;
import java.util.Random;


class MojaPostać implements Postać {
    private int szerokość, wysokość;

    MojaPostać(int szerokość, int wysokość) {
        this.szerokość = szerokość;
        this.wysokość = wysokość;
    }
    public int dajWysokość() {
        return this.wysokość;
    }

    public int dajSzerokość() {
        return this.szerokość;
    }
}

class PostaćThread extends Thread {
    int x, y, id;
    MojaPlansza plansza;
    MojaPostać postać;

    PostaćThread(int id, int x, int y, MojaPlansza plansza, MojaPostać postać) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.plansza = plansza;
        this.postać = postać;
    }

    @Override
    public void run() {
        try {
            plansza.postaw(postać, x, y);
            plansza.drukujPlanszę();
            if(id == 1) {
                sleep(3000);
                plansza.usuń(postać);
                plansza.drukujPlanszę();
            }
            else if(id == 2) {
                sleep(1000);
                plansza.przesuń(postać, Kierunek.GÓRA);
                plansza.drukujPlanszę();
                sleep(1000);
                plansza.przesuń(postać, Kierunek.LEWO);
                plansza.drukujPlanszę();
                sleep(1000);
                plansza.przesuń(postać, Kierunek.LEWO);
            }
            else if(id == 3 || id == 4) {
                while(true) {
                    sleep(1000);
                    Kierunek kierunek;
                    Random random = new Random();
                    int losowa = random.nextInt(4);
                    if (losowa == 0)
                        kierunek = Kierunek.GÓRA;
                    else if (losowa == 1)
                        kierunek = Kierunek.DÓŁ;
                    else if (losowa == 2)
                        kierunek = Kierunek.LEWO;
                    else
                        kierunek = Kierunek.PRAWO;
                    try {
                        plansza.przesuń(postać, kierunek);
                        plansza.drukujPlanszę();
                    }
                    catch(Exception e) {
                        System.out.println("Zły kierunek, próbuj dalej");
                    }
                }
            }
            else if(id == 5) {
                sleep(1000);
                plansza.przesuń(postać, Kierunek.DÓŁ);
            }
            else if(id == 6) {
                plansza.przesuń(postać, Kierunek.GÓRA);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DeadlockException e) {
            System.out.println("DEADLOCK EXCEPTION");
            return;
        }
    }
}

public class Testy {

    public static void main(String[] args) throws InterruptedException {
        int test = 2;
        if(test == 1) {
            MojaPlansza plansza = new MojaPlansza(5, 5);
            ArrayList<PostaćThread> wątki = new ArrayList<PostaćThread>();
            wątki.add(new PostaćThread(1, 0, 0, plansza, new MojaPostać(2, 2)));
            wątki.add(new PostaćThread(2, 2, 2, plansza, new MojaPostać(2, 2)));
            for (PostaćThread postać : wątki)
                postać.start();
        }
        else if(test == 2) {
            MojaPlansza plansza = new MojaPlansza(15, 15);
            ArrayList<PostaćThread> wątki = new ArrayList<PostaćThread>();
            wątki.add(new PostaćThread(3, 3, 4, plansza, new MojaPostać(1, 1)));
            wątki.add(new PostaćThread(4, 2, 2, plansza, new MojaPostać(1, 1)));
            for (PostaćThread postać : wątki)
                postać.start();
        }
        else if(test == 3) {
            MojaPlansza plansza = new MojaPlansza(5, 5);
            ArrayList<PostaćThread> wątki = new ArrayList<PostaćThread>();
            wątki.add(new PostaćThread(5, 0, 0, plansza, new MojaPostać(1, 1)));
            wątki.add(new PostaćThread(6, 1, 0, plansza, new MojaPostać(1, 1)));
            for (PostaćThread postać : wątki)
                postać.start();
        }
    }
}
