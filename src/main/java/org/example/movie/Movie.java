package org.example.movie;

public class Movie {
    private int id;
    private String title;
    private Category category;
    private String description;
    private String year;


    public Movie(int id, String title, Category category, String description,  String year) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.year = year;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Category getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return """
           ─────────────────────────────
           🎬 %s (%s)
           ID: %d
           Categoria: %s
           
           %s
           ─────────────────────────────
           """.formatted(
                title,
                year,
                id,
                category,
                description
        );
    }

    public String toStringMinor() {
        return "[ID: " + id + ", " + title + "]";
    }
}
