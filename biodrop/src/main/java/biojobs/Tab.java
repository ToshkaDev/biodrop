package biojobs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vadim on 12/16/17.
 */

@Entity
@Table(name="tab")
public class Tab {
    @Id
    @SequenceGenerator(name="pk_sequence_biodrop", sequenceName="tab_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pk_sequence_biodrop")
    @Column(name="TAB_ID")
    private int tabId;

    @NotNull
    @Column(name="TAB_NAME")
    private String tabName;

    @NotNull
    @Column(name="TAB_TEXT")
    private String tabText;

    @NotNull
    @Column(name="TAB_LINK")
    private String tabLink;

    @OneToMany(mappedBy="tab", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<BioDrop> bioDropList = new LinkedList<>();

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabText() {
        return tabText;
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }

    public List<BioDrop> getBioDropList() {
        return bioDropList;
    }

    public void setBioDropList(List<BioDrop> bioDropList) {
        this.bioDropList = bioDropList;
    }

    public String getTabLink() {
        return tabLink;
    }

    public void setTabLink(String tabLink) {
        this.tabLink = tabLink;
    }
}
