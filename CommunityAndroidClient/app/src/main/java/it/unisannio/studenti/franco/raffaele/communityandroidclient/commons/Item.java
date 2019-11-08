package it.unisannio.studenti.franco.raffaele.communityandroidclient.commons;

public class Item {
    public Item(String activity, String description) {
        this.activity = activity;
        this.description = description;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String activity;
    private String description;
}
