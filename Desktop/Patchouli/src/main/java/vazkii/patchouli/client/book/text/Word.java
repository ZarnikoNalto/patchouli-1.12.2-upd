package vazkii.patchouli.client.book.text;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class Word {
	private final Book book;
	private final GuiBook gui;
	private final FontRenderer font;
	private final int x, y, width, height;
	private final String text;
	private final int color;
	private final String codes;
	private final List<Word> linkCluster;
	private final String tooltip;
	private final Runnable onClick;

	public Word(GuiBook gui, Span span, String text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.font = span.font;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.text = text;
		this.color = span.color;
		this.codes = span.codes;
		this.onClick = span.onClick;
		this.linkCluster = cluster;
		this.tooltip = span.tooltip;
	}

	public void render(int mouseX, int mouseY) {
		String renderTarget = codes + text;
		int renderColor = color;
		if(isClusterHovered(mouseX, mouseY)) {
			if (onClick != null)
				renderColor = book.linkHoverColor;
			if (!tooltip.isEmpty())
				gui.setTooltip(tooltip);
		}

		font.drawString(renderTarget, x, y, renderColor);
	}

	public void click(int mouseX, int mouseY, int mouseButton) {
		if(onClick != null && mouseButton == 0 && isHovered(mouseX, mouseY))
			onClick.run();
	}

	private boolean isHovered(int mouseX, int mouseY) {
		return gui.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height);
	}

	private boolean isClusterHovered(int mouseX, int mouseY) {
		if(linkCluster == null)
			return isHovered(mouseX, mouseY);

		for(Word w : linkCluster)
			if(w.isHovered(mouseX, mouseY))
				return true;

		return false;
	}
}
