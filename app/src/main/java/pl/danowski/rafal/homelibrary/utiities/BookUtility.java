package pl.danowski.rafal.homelibrary.utiities;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import pl.danowski.rafal.homelibrary.R;
import pl.danowski.rafal.homelibrary.adapters.BookGridAdapter;
import pl.danowski.rafal.homelibrary.model.Bind;
import pl.danowski.rafal.homelibrary.model.Position;
import pl.danowski.rafal.homelibrary.model.Room;
import pl.danowski.rafal.homelibrary.model.User;

public class BookUtility {

    private static boolean initialized = false;
    private static List<Bind> binds;
    private static View headView;

    public static void initialize(View view) {
        assert !initialized : "Already initialized!";

        initialized = true;
        binds = new ArrayList<>();
        headView = view;

        fillList();
    }

    private static void fillList() {
        assert initialized : "Not initialized!";

        for(int i = 0; i < 2; ++i) {
            addBind();
        }
    }

    public static void printBooks() {
        assert initialized : "Class not initialized!";

        GridView gridView = headView.findViewById(R.id.booksGrid);
        ArrayAdapter<Bind> adapter = new BookGridAdapter(headView.getContext(), (ArrayList<Bind>) binds);
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {

            }
        });
    }

    public static void addBind() {
        Random rand = new Random();
        int score = rand.nextInt(10) + 1;
        int intChar = rand.nextInt(26) + 65;
        char c = (char) intChar;
        boolean fav = rand.nextBoolean();

        User user1 = new User("admin", "e00cf25ad42683b3df678c61f42c6bda","Anon", "Anonski", false, "anon@yopmail.com");
        Room room1 = new Room("Salon", "S", "#ffffff");
        Position pos1 = new Position(c + "Alicja w krainie czarów", c + "Jakiś koleś",
                "Wydawnictwo Znak", 1, 1, "N/A", 1111, 1998);

        Bind bind1 = new Bind(user1, pos1, room1, 1, "Brak komentarza", fav, score, true);

        binds.add(bind1);
        printBooks();
    }

    public static Bind getBindFromPosition(int position) {
        return binds.get(position);
    }

    @SuppressLint("NewApi")
    public static void sortByScoreAsc() {
//        binds.sort(Comparator.comparingInt(Bind::getRating));
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b1.getRating() - b2.getRating();
            }
        });
        printBooks();
    }

    @SuppressLint("NewApi")
    public static void sortByScoreDesc() {
//        binds.sort(Comparator.comparingInt(Bind::getRating).reversed());
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b2.getRating() - b1.getRating();
            }
        });
        printBooks();
    }

    @SuppressLint("NewApi")
    public static void sortByTitleAsc() {
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b1.getBook().getTitle().compareTo(b2.getBook().getTitle());
            }
        });
        printBooks();
    }

    @SuppressLint("NewApi")
    public static void sortByTitleDesc() {
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b2.getBook().getTitle().compareTo(b1.getBook().getTitle());
            }
        });
        printBooks();
    }

    @SuppressLint("NewApi")
    public static void sortByAuthorAsc() {
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b1.getBook().getAuthor().compareTo(b2.getBook().getAuthor());
            }
        });
        printBooks();
    }

    @SuppressLint("NewApi")
    public static void sortByAuthorDesc() {
        binds.sort(new Comparator<Bind>() {
            @Override
            public int compare(Bind b1, Bind b2) {
                return b2.getBook().getAuthor().compareTo(b1.getBook().getAuthor());
            }
        });
        printBooks();
    }
}
