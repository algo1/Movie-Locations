
package inplokesh.ac.iitkgp.cse.movielocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("actor_1")
    @Expose
    private String actor1;
    @SerializedName("actor_2")
    @Expose
    private String actor2;
    @SerializedName("director")
    @Expose
    private String director;
    @SerializedName("distributor")
    @Expose
    private String distributor;
    @SerializedName("fun_facts")
    @Expose
    private String funFacts;
    @SerializedName("locations")
    @Expose
    private String locations;
    @SerializedName("production_company")
    @Expose
    private String productionCompany;
    @SerializedName("release_year")
    @Expose
    private String releaseYear;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("writer")
    @Expose
    private String writer;

    /**
     * @return The actor1
     */
    public String getActor1() {
        return actor1;
    }

    /**
     * @param actor1 The actor_1
     */
    public void setActor1(String actor1) {
        this.actor1 = actor1;
    }

    /**
     * @return The actor2
     */
    public String getActor2() {
        return actor2;
    }

    /**
     * @param actor2 The actor_2
     */
    public void setActor2(String actor2) {
        this.actor2 = actor2;
    }

    /**
     * @return The director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @param director The director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * @return The distributor
     */
    public String getDistributor() {
        return distributor;
    }

    /**
     * @param distributor The distributor
     */
    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    /**
     * @return The funFacts
     */
    public String getFunFacts() {
        return funFacts;
    }

    /**
     * @param funFacts The fun_facts
     */
    public void setFunFacts(String funFacts) {
        this.funFacts = funFacts;
    }

    /**
     * @return The locations
     */
    public String getLocations() {
        return locations;
    }

    /**
     * @param locations The locations
     */
    public void setLocations(String locations) {
        this.locations = locations;
    }

    /**
     * @return The productionCompany
     */
    public String getProductionCompany() {
        return productionCompany;
    }

    /**
     * @param productionCompany The production_company
     */
    public void setProductionCompany(String productionCompany) {
        this.productionCompany = productionCompany;
    }

    /**
     * @return The releaseYear
     */
    public String getReleaseYear() {
        return releaseYear;
    }

    /**
     * @param releaseYear The release_year
     */
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The writer
     */
    public String getWriter() {
        return writer;
    }

    /**
     * @param writer The writer
     */
    public void setWriter(String writer) {
        this.writer = writer;
    }

}