package gra;

class Pole {
    private Postać postać;
    private int x, y;
    private int xKon, yKon; // do ktorych wspolrzednych postac zajmuje miejsce na planszy
    private Kierunek ruch;
    private boolean naPlanszy;

    public Pole() {
        this.postać = null;
        naPlanszy = false;
    }

    public Pole(Postać postać, int x, int y) {
        this.postać = postać;
        this.x = x;
        this.y = y;
        naPlanszy = false;
        policzKoncowe();
    }

    private void policzKoncowe() {
        xKon = x + postać.dajSzerokość() - 1;
        yKon = y + postać.dajWysokość() - 1;
    }

    public void przesuń(Kierunek kierunek) {
        switch (kierunek) {
            case GÓRA:
                y--;
                yKon--;
                break;
            case DÓŁ:
                y++;
                yKon++;
                break;
            case LEWO:
                x--;
                xKon--;
                break;
            case PRAWO:
                x++;
                xKon++;
                break;
        }
    }

    public Postać getPostać() {
        return postać;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getxKon() {
        return xKon;
    }

    public int getyKon() {
        return yKon;
    }

    public void setNaPlanszy(boolean naPlanszy) {
        this.naPlanszy = naPlanszy;
    }

    public void setRuch(Kierunek ruch) { this.ruch = ruch;}

    public Kierunek getRuch() {
        return ruch;
    }

    public boolean getNaPlanszy() {
        return naPlanszy;
    }

    @Override
    public String toString() {
        return "Pole{" +
                "postać=" + postać +
                ", x=" + x +
                ", y=" + y +
                ", xKon=" + xKon +
                ", yKon=" + yKon +
                ", ruch=" + ruch +
                ", naPlanszy=" + naPlanszy +
                '}';
    }
}
