package vazkii.patchouli.client.book.page;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ValidationUtils;

public class PageQuest extends PageWithText {

	String trigger, title;
	
	transient BookTextRenderer infoText;
	transient boolean isManual;
	transient GuiButton button;
	transient int footerY;
	
	@Override
	public int getTextHeight() {
		return 22;
	}
	
	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		if(trigger != null && !trigger.isEmpty()) {
			ValidationUtils.validateAdvancement(trigger);
			isManual = false;
		} else isManual = true;
	}
	
	public boolean isCompleted(Book book) {
		return isManual 
				? PersistentData.data.getBookData(book).completedManualQuests.contains(entry.getResource().toString())
				: trigger != null && !trigger.isEmpty() && ClientAdvancements.hasDone(trigger);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		if(isManual) {
			addButton(button = new GuiButton(0, GuiBook.PAGE_WIDTH / 2 - 50, GuiBook.PAGE_HEIGHT - 35, 100, 20, ""));
			updateButtonText();
		}
	}
	
	private void updateButtonText() {
		boolean completed = isCompleted(parent.book);
		String s = I18n.format(completed ? "patchouli.gui.lexicon.mark_incomplete" : "patchouli.gui.lexicon.mark_complete");
		button.displayString = s;
	}
	
	@Override
	protected void onButtonClicked(GuiButton button) {
		if(button == this.button) {
			String res = entry.getResource().toString();
			BookData data = PersistentData.data.getBookData(parent.book);
			
			if(data.completedManualQuests.contains(res))
				data.completedManualQuests.remove(res);
			else data.completedManualQuests.add(res);
			PersistentData.save();
			
			updateButtonText();
			entry.markReadStateDirty();
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		super.render(mouseX, mouseY, pticks);
		
		parent.drawCenteredStringNoShadow(title == null || title.isEmpty() ? I18n.format("patchouli.gui.lexicon.objective") : title, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		GuiBook.drawSeparator(book, 0, 12);
		
		if(!isManual) {
			GuiBook.drawSeparator(book, 0, GuiBook.PAGE_HEIGHT - 25);
			
			boolean completed = isCompleted(parent.book);
			String s = I18n.format(completed ? "patchouli.gui.lexicon.complete" : "patchouli.gui.lexicon.incomplete");
			int color = completed ? 0x008b1a : book.headerColor;
			
			parent.drawCenteredStringNoShadow(s, GuiBook.PAGE_WIDTH / 2, GuiBook.PAGE_HEIGHT - 17, color);
		}
			
	}

}
