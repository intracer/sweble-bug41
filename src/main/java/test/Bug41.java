package test;

import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtTableCell;
import org.sweble.wikitext.parser.nodes.WtTableRow;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.parser.LinkTargetException;


public class Bug41 {

    static WikiConfig config = DefaultConfigEnWp.generate();

    public static void main(String[] args) throws LinkTargetException, EngineException {

        String text =
                "{|\n" +
                        "|-\n" +
                        "|<translate><!--T:1-->\n" +
                        "cell1</translate> ||cell2\n" +
                        "|}";

        System.out.println("\n *** bug, one cell *** \n");
        printCells(text);

        String shouldParseAs = text.replace("-->\n", "-->");

        System.out.println("\n *** expected, two cells *** \n");
        printCells(shouldParseAs);
    }

    private static void printCells(String text) throws LinkTargetException, EngineException {
        PageId title = new PageId(PageTitle.make(config, "title"), -1);
        EngProcessedPage page = new WtEngineImpl(config).postprocess(title, text, null);

        WtTableRow row = (WtTableRow) findNode(page, WtTableRow.class);
        int cells = row.getBody().size();
        System.out.println("Cells: " + cells);

        for (int cellIndex = 0; cellIndex < cells; cellIndex++) {
            printCell(row, cellIndex);
        }
    }

    private static void printCell(WtTableRow row, int cellIndex) {
        WtTableCell cell = (WtTableCell) row.getBody().get(cellIndex);
        WtText cellText = (WtText) findNode(cell, WtText.class);
        System.out.println(cellText.toString());
    }

    private static WtNode findNode(WtNode node, Class<? extends WtNode> clazz) {
        if (clazz.isAssignableFrom(node.getClass()))
            return node;
        else {
            for (WtNode child : node) {
                WtNode row = findNode(child, clazz);
                if (row != null) {
                    return row;
                }
            }
            return null;
        }
    }

}
