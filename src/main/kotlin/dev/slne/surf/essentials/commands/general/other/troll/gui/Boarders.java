package dev.slne.surf.essentials.commands.general.other.troll.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class Boarders {
    /**
     * The left boarder {@link OutlinePane} using {@link TrollGuiItems#boarder()}
     *
     * @return the boarder
     */
    public OutlinePane LEFT_BOARDER(){
        val pane = new OutlinePane(0, 0, 1, 6);
        pane.addItem(TrollGuiItems.boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The right boarder {@link OutlinePane} using {@link TrollGuiItems#boarder()}
     *
     * @return the boarder
     */
    public OutlinePane RIGHT_BOARDER(){
        val pane = new OutlinePane(8, 0, 1, 6);
        pane.addItem(TrollGuiItems.boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The bottom boarder {@link PatternPane} using {@link TrollGuiItems#boarder()}
     *
     * @return the boarder
     */
    public PatternPane BOTTOM_BOARDER(){
        val pattern = new Pattern("0012300");
        val pane = new PatternPane(1, 5, 7, 1, pattern);

        pane.bindItem('0', TrollGuiItems.boarder());
        pane.bindItem('1', TrollGuiItems.BACKWARD_BUTTON());
        pane.bindItem('2', TrollGuiItems.CLOSE_BUTTON());
        pane.bindItem('3', TrollGuiItems.FORWARD_BUTTON());

        return pane;
    }

    /**
     * The bottom boarder {@link PatternPane} that can change between {@link PaginatedPane}´s using {@link TrollGuiItems#boarder()}
     *
     * @param paginatedPane the {@link PaginatedPane} from witch the pages should be changed
     * @param gui the main {@link ChestGui}
     *
     * @return the boarder
     */
    public PatternPane BOTTOM_BOARDER(PaginatedPane paginatedPane, ChestGui gui){
        val pattern = new Pattern("0012300");
        val pane = new PatternPane(1, 5, 7, 1, pattern);

        pane.bindItem('0', TrollGuiItems.boarder());
        pane.bindItem('2', TrollGuiItems.CLOSE_BUTTON());

        val forwardButton = TrollGuiItems.FORWARD_BUTTON();
        forwardButton.setAction(inventoryClickEvent -> {
            if (paginatedPane.getPages() <= paginatedPane.getPage() + 1) return;
            paginatedPane.setPage(paginatedPane.getPage() + 1);
            gui.update();
        });
        pane.bindItem('3', forwardButton);

        val backwardButton = TrollGuiItems.BACKWARD_BUTTON();
        backwardButton.setAction(inventoryClickEvent -> {
            if (0 > paginatedPane.getPage() - 1) return;
            paginatedPane.setPage(paginatedPane.getPage() - 1);
            gui.update();
        });
        pane.bindItem('1', backwardButton);

        return pane;
    }

    /**
     * the upper boarder {@link OutlinePane} using {@link TrollGuiItems#boarder()}
     *
     * @return the boarder
     */
    public OutlinePane UPPER_BOARDER(){
        val pane = new OutlinePane(1, 0, 7, 1);
        pane.addItem(TrollGuiItems.boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * set all boarders for a {@link ChestGui}
     *
     * @param gui the {@link ChestGui}
     */
    public void setAllBoarders(ChestGui gui){
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER());
    }

    /**
     * set all boarders for a {@link ChestGui} with a {@link PaginatedPane} interface
     *
     * @param gui the {@link ChestGui}
     * @param paginatedPane the {@link PaginatedPane}
     */
    public void setAllBoarders(ChestGui gui, PaginatedPane paginatedPane){
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER(paginatedPane, gui));
    }
}
