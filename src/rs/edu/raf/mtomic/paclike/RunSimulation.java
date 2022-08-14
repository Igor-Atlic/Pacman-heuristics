package rs.edu.raf.mtomic.paclike;

import rs.edu.raf.mtomic.paclike.agent.Pair;
import rs.edu.raf.mtomic.paclike.agent.player.PlayerOne;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Test klasa:
 * <p>
 * Ovde možete ubaciti proizvoljni sistem za optimizaciju.
 * <p>
 * Zadatak je naslediti klasu Player i implementirati metodu generateNextMove,
 * koja će za svaki poziv vratiti jednu od sledećih metoda:
 * this::goUp, this::goLeft, this::goDown, this::goRight
 * te na taj način upravljati igračem u lavirintu.
 * <p>
 * Može se i (umesto nove klase) izmeniti klasa PlayerOne, koja sada samo ide levo.
 * <p>
 * U metodi se smeju koristiti svi dostupni geteri, ali ne smeju se
 * koristiti metode koje na bilo koji način menjaju stanje protivničkih
 * agenata ili stanje igre.
 * <p>
 * Matrica fields iz gameState: prvi indeks je kolona (X), drugi je vrsta (Y)
 * <p>
 * Zadatak: koristiti genetski algoritam za optimizaciju parametara
 * koji mogu odlučivati o sledećem potezu igrača. Generisanje poteza se
 * vrši na svaki frejm. Cilj je pokupiti svih 244 tačkica iz lavirinta.
 * Ako igrač naleti na protivnika, igra se prekida.
 * <p>
 * Savet: pogledajte kako Ghost agenti odlučuju o tome kada treba napraviti
 * skretanje (mada oni imaju jednostavna ponašanja) u njihovoj metodi Ghost::playMove.
 * <p>
 * Takođe, u implementacijama njihove metode calculateBest mogu se videti
 * primeri korišćenja GameState, iz koga se čitaju svi parametri.
 * <p>
 * Konačno stanje igre generiše se pokretanjem igre preko konstruktora i
 * pozivom join(), pa onda getTotalPoints().
 * <p>
 * Igrač se inicijalizuje GameState-om null, a PacLike će obezbediti
 * odgovarajuće stanje.
 * <p>
 * Ukoliko želite da pogledate simulaciju igre, promenite polje render
 * u klasi PacLike na true, a fps podesite po želji (ostalo ne treba
 * dirati).
 * <p>
 * Ograničenja:
 * - Svi parametri koji se koriste u generateNextMove() moraju biti
 * ili nepromenjeni (automatski generisani i menjani od strane igre),
 * ili inicijalizovani pomoću genetskog algoritma, ili eventualno
 * ako dodajete nove promenljive u klasu inicijalizovani u konstruktoru.
 * <p>
 * - Kalkulacije pomoću tih parametara i na osnovu onoga što igrač vidi
 * na osnovu GameState klase (i svih getera odatle i od objekata do
 * kojih odatle može da se dospe) su dozvoljene i poželjne; nije
 * dozvoljeno oslanjati se na unutrašnju logiku drugih agenata i hardkodovati
 * ponašanja ili šablone koji postoje za ovu igru (mada je engine
 * dosta promenjen u odnosu na original, iako liči, tako da je
 * većina šablona u suštini neupotrebljiva).
 **/
public class RunSimulation {

    public static void main(String[] args) {
        try {
            FileWriter fileWriter = new FileWriter("najbolji.txt");
            FileWriter fileWriter2 = new FileWriter("najboljigeneracije.txt");
            FileWriter fileWriter1 = new FileWriter("text.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            PrintWriter printWriter2 = new PrintWriter(fileWriter2);
            PrintWriter printWriter1 = new PrintWriter(fileWriter1);
            int [] muN = {1,1,2,2,4};
            int [] lmbN = {1,2,2,4,8};
            double [] sigmaN = {0.5,1,2};
            for (int iter  = 0; iter < 5; iter++) {
                for (int sig = 0; sig<3;sig++) {
                    Pair iterB = null;
                    for (int j = 0; j < 10; j++) {
                        double sigma = sigmaN[sig];
                        int br = 0;
                        int best = 0;
                        Pair bestH = null;
                        int stag = 0;
                        int lmb = lmbN[iter];
                        int mu = muN[iter];
                        int bestN = 0;
                        List<Pair> pop = new ArrayList<>();
                        for (int i = 0; i < mu; i++) {
                            double h1, h2;
                            h1 = new Random().nextDouble() * 10;
                            h2 = (15 - 5) * new Random().nextDouble() - 5;
                            PacLike pacLike = new PacLike(new PlayerOne(null, h1, h2));
                            pacLike.join();
                            PlayerOne tem = (PlayerOne) pacLike.getPlayer();
                            pop.add(new Pair(tem.getH1(), tem.getH2(), pacLike.getTotalPoints()));
                        }
                        while (true) {
                            br++;
                            for (int i = 0; i < lmb; i++) {
                                int r = new Random().nextInt(mu);
                                double hm1 = pop.get(r).getH1() + sigma * new Random().nextGaussian();
                                if (hm1 < 0) {
                                    hm1 = 0;
                                } else if (hm1 > 10) {
                                    hm1 = 10;
                                }
                                double hm2 = pop.get(r).getH2() + sigma * new Random().nextGaussian();
                                PacLike pacLike1 = new PacLike(new PlayerOne(null, hm1, hm2));
                                //pacLike1.join();
                                pacLike1.join(100);
                                pop.add(new Pair(hm1, hm2, pacLike1.getTotalPoints()));

                            }
                            pop.sort(Comparator.comparingInt(Pair::getTrosak).reversed());
                            pop = pop.subList(0, mu);
                            if (pop.get(0).getTrosak() > best) {
                                best = pop.get(0).getTrosak();
                                stag = 0;
                                bestH = pop.get(0);
                                bestN++;
                                printWriter2.println("Iteracija = " + br + " : " + iter +  ", sig: "+sig + ", " + bestH);
                                printWriter2.flush();
                            } else {
                                stag++;
                                if (stag >= 50) {
                                    break;
                                }
                            }
                            if (br % 10 == 0 && br >= 10) {
                                if (bestN < mu / 2) {
                                    sigma *= 0.85;
                                } else {
                                    sigma /= 0.85;
                                }
                                bestN = 0;
                            }
                        }
                        if (iterB == null) {
                            iterB = bestH;
                        } else if (bestH.getTrosak() > iterB.getTrosak()) {
                            iterB = bestH;
                        }
                        System.out.println("Iteracija = " + br + ", " + bestH);
                        printWriter1.println("Iteracija = " + br + " : " + iter +  ", sig: "+sig + ", " + bestH);
                        printWriter1.flush();
                    }
                    printWriter.println("Iteracija = " + iter + ", "+ sig + ", " + iterB);
                    printWriter.flush();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}