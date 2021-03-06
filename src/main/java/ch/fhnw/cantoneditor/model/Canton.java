package ch.fhnw.cantoneditor.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import ch.fhnw.cantoneditor.datautils.BaseModel;
import ch.fhnw.cantoneditor.datautils.Initable;
import ch.fhnw.cantoneditor.datautils.Searchable;
import ch.fhnw.observation.ObservableList;

import com.google.gson.annotations.Expose;

public class Canton extends BaseModel implements Initable, Searchable {
    public static final String NAME_PROPERTY = "name";
    public static final String SHORTCUT_PROPERTY = "shortCut";
    public static final String NRCOUNCILSEATS_PROPERTY = "ssknrCouncilSeats";
    public static final String ENTRYYEAR_PROPERTY = "entryYear";
    public static final String NRFOREIGNERS_PROPERTY = "nrForeigners";
    public static final String LANGUAGEID_PROPERTY = "languageId";
    public static final String CAPITAL_PROPERTY = "capital";
    public static final String AREA_PROPERTY = "area";
    public static final String INHABITANTDENSITY_PROPERTY = "inHabitantDensity";
    public static final String NRCOMMUNES_PROPERTY = "nrCommunes";
    public static final String NRINHABITANTS_PROPERTY = "nrInhabitants";

    private String name;
    private String shortCut;

    private int nrCouncilSeats;
    private int entryYear;
    private double nrForeigners;
    private int nrInhabitants;

    private String languageString;// 4 Gson

    @Expose(serialize = false, deserialize = false)
    private transient ObservableList<Language> languageId = new ObservableList<>();
    private String capital;
    private double area;

    // Einwohnerdichte
    private double inHabitantDensity;
    private int nrCommunes;

    private String communeString;// 4 gson

    @Expose(serialize = false, deserialize = false)
    private transient ObservableList<String> communes = new ObservableList<>();

    @Expose(serialize = false, deserialize = false)
    private transient boolean isInited = false;

    public void init() {
        if (isInited)
            return;

        if (languageString != null && !languageString.isEmpty()) {

            java.util.List<Language> lngIds = Arrays
                    .asList(languageString.split(","))
                    .stream()
                    .map(s -> Integer.parseInt(s))
                    .map(s -> Language.getAllLanguages().stream().filter(l -> l.getId() == s.intValue()).findFirst()
                            .get()).collect(Collectors.toList());

            languageId = new ObservableList<Language>(lngIds);
            languageString = null;

        }

        if (communeString != null && !communeString.isEmpty()) {

            communes = new ObservableList<String>(Arrays.asList(communeString.split(",")));
            communeString = null;
        }

        isInited = true;
    }

    public static Canton createNew() {
        return new Canton();
    }

    public String getName() {
        this.notifyPropertyRead(NAME_PROPERTY);
        return name;
    }

    public void setName(String name) {
        if (name != this.name) {
            Object oldValue = this.name;
            this.name = name;
            this.pcs.firePropertyChange(NAME_PROPERTY, oldValue, name);
        }
    }

    public String getShortCut() {
        this.notifyPropertyRead(SHORTCUT_PROPERTY);
        return shortCut;
    }

    public void setShortCut(String shortCut) {
        if (shortCut != this.shortCut) {
            Object oldValue = this.shortCut;
            this.shortCut = shortCut;
            this.pcs.firePropertyChange(SHORTCUT_PROPERTY, oldValue, shortCut);
        }
    }

    public int getNrCouncilSeats() {
        this.notifyPropertyRead(NRCOUNCILSEATS_PROPERTY);
        return nrCouncilSeats;
    }

    public void setNrCouncilSeats(int nrCouncilSeats) {
        if (nrCouncilSeats != this.nrCouncilSeats) {
            Object oldValue = this.nrCouncilSeats;
            this.nrCouncilSeats = nrCouncilSeats;
            this.pcs.firePropertyChange(NRCOUNCILSEATS_PROPERTY, oldValue, nrCouncilSeats);
        }
    }

    public int getEntryYear() {
        this.notifyPropertyRead(ENTRYYEAR_PROPERTY);
        return entryYear;
    }

    public void setEntryYear(int entryYear) {
        if (entryYear != this.entryYear) {
            Object oldValue = this.entryYear;
            this.entryYear = entryYear;
            this.pcs.firePropertyChange(ENTRYYEAR_PROPERTY, oldValue, entryYear);
        }
    }

    public double getNrForeigners() {
        this.notifyPropertyRead(NRFOREIGNERS_PROPERTY);
        return nrForeigners;// For whatever reason, the field is between 0 and 100, yet
                            // should be between 0 and 1
    }

    public void setNrForeigners(double nrForeigners) {
        // For whatever reason, the field is between 0 and 100, yet should be between 0 and 1
        if (nrForeigners != this.nrForeigners) {
            Object oldValue = this.nrForeigners;
            this.nrForeigners = nrForeigners;
            this.pcs.firePropertyChange(NRFOREIGNERS_PROPERTY, oldValue, this.nrForeigners);

        }
    }

    public ObservableList<Language> getLanguages() {

        return languageId;
    }

    public String getCapital() {

        this.notifyPropertyRead(CAPITAL_PROPERTY);
        return capital;
    }

    public void setCapital(String capital) {
        if (capital != this.capital) {
            Object oldValue = this.capital;
            this.capital = capital;
            this.pcs.firePropertyChange(CAPITAL_PROPERTY, oldValue, capital);
        }
    }

    public double getArea() {
        this.notifyPropertyRead(AREA_PROPERTY);
        return area;
    }

    public void setArea(double area) {
        if (area != this.area) {
            Object oldValue = this.area;
            this.area = area;
            this.pcs.firePropertyChange(AREA_PROPERTY, oldValue, area);
        }
    }

    public double getInHabitantDensity() {
        this.notifyPropertyRead(INHABITANTDENSITY_PROPERTY);
        return inHabitantDensity;
    }

    public void setInHabitantDensity(double inHabitantDensity) {
        if (inHabitantDensity != this.inHabitantDensity) {
            Object oldValue = this.inHabitantDensity;
            this.inHabitantDensity = inHabitantDensity;
            this.pcs.firePropertyChange(INHABITANTDENSITY_PROPERTY, oldValue, inHabitantDensity);
        }
    }

    public int getNrCommunes() {
        return this.communes.size();
    }

    public ObservableList<String> getCommunes() {

        return communes;
    }

    public int getNrInhabitants() {
        this.notifyPropertyRead(NRINHABITANTS_PROPERTY);
        return nrInhabitants;
    }

    public void setNrInhabitants(int nrInhabitants) {
        if (nrInhabitants != this.nrInhabitants) {
            Object oldValue = this.nrInhabitants;
            this.nrInhabitants = nrInhabitants;
            this.pcs.firePropertyChange(NRINHABITANTS_PROPERTY, oldValue, nrInhabitants);
        }
    }

    @Override
    public int hashCode() {
        if (this.id != 0) { // Not a newly created object
            return this.id;// Primary Key
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (this.id == 0)
            return false;// Two newly created Communes are newer the same
        if (obj instanceof Canton) {
            return ((Canton) obj).getId() == this.id;
        }
        return false;
    };

    @Override
    public String toString() {
        return this.getName();
    }

    /** Creates a new canton with the same properties as this canton */
    public Canton copyToNew() {
        Canton c = new Canton();
        c.name = this.name;
        c.shortCut = this.shortCut;
        c.nrCouncilSeats = this.nrCouncilSeats;
        c.entryYear = this.entryYear;
        c.nrForeigners = this.nrForeigners;
        c.nrInhabitants = this.nrInhabitants;
        c.capital = this.capital;
        c.area = this.area;
        c.inHabitantDensity = this.inHabitantDensity;
        c.nrCommunes = this.nrCommunes;

        for (Language lng : this.languageId) {
            c.getLanguages().add(lng);
        }
        for (String comm : this.communes) {
            c.getCommunes().add(comm);
        }
        return c;
    }

    public void beforeSerialize() {
        communeString = String.join(",", this.communes);
        languageString = String.join(",", languageId.stream().map(s -> s.getId() + "").collect(Collectors.toList()));
    }

    @Override
    public String[] getSearchStrings() {
        return new String[] { this.getName().toLowerCase(), this.getShortCut().toLowerCase(),
                this.getCapital().toLowerCase() };
    }
}
