package vazkii.patchouli.client.book.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageRelations extends PageWithText {

	List<String> entries;
	String title;

	transient List<BookEntry> entryObjs;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);

		entryObjs = entries.stream()
				.map((s) -> s.contains(":") ? new ResourceLocation(s) : new ResourceLocation(book.getModNamespace(), s))
				.map((res) -> book.contents.entries.get(res))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		List<BookEntry> displayedEntries = new ArrayList<>(entryObjs);
		displayedEntries.removeIf(BookEntry::shouldHide);
		Collections.sort(displayedEntries);
		for(int i = 0; i < displayedEntries.size(); i++) {
			GuiButton button = new GuiButtonEntry(parent, 0, 20 + i * 11, displayedEntries.get(i), i);
			addButton(button);
		}
	}
	
	@Override
	protected void onButtonClicked(GuiButton button) {
		if(button instanceof GuiButtonEntry)
			GuiBookEntry.displayOrBookmark(parent, ((GuiButtonEntry) button).getEntry());
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		parent.drawCenteredStringNoShadow(title == null || title.isEmpty() ? I18n.format("patchouli.gui.lexicon.relations") : title, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		GuiBook.drawSeparator(book, 0, 12);
		
		super.render(mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 22 + entryObjs.size() * 11;
	}

}
