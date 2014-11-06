package tw.catcafe.materialtabs;

/**
 * Created by Davy on 14/11/5.
 */
public interface TabListener {
    public void onTabSelected(TabItem tab);

    public void onTabReselected(TabItem tab);

    public void onTabUnselected(TabItem tab);
}