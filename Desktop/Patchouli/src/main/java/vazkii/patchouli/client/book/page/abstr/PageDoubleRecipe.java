package vazkii.patchouli.client.book.page.abstr;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public abstract class PageDoubleRecipe<T> extends PageWithText {

	@SerializedName("recipe")
	String recipeRaw;
	@SerializedName("recipe2")
	String recipe2Raw;
	String title;

	protected transient T recipe1, recipe2;
	protected transient String title1, title2;
	
	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		recipe1 = loadRecipe(entry, recipeRaw);
		recipe2 = loadRecipe(entry, recipe2Raw);
		
		if(recipe1 == null && recipe2 != null) {
			recipe1 = recipe2;
			recipe2 = null;
		}
		
		boolean customTitle = title != null && !title.isEmpty();
		title1 = !customTitle ? getRecipeOutput(recipe1).getDisplayName() : title;
		title2 = "-";
		if(recipe2 != null) {
			title2 = !customTitle ? getRecipeOutput(recipe2).getDisplayName() : "";
			if(title1.equals(title2))
				title2 = "";
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		if(recipe1 != null) {
			int recipeX = getX();	
			int recipeY = getY();
			drawRecipe(recipe1, recipeX, recipeY, mouseX, mouseY, false);
			
			if(recipe2 != null)
				drawRecipe(recipe2, recipeX, recipeY + getRecipeHeight() - (title2.isEmpty() ? 10 : 0), mouseX, mouseY, true);
		}
		
		super.render(mouseX, mouseY, pticks);
	}
	
	@Override
	public int getTextHeight() {
		return getY() + getRecipeHeight() * (recipe2 == null ? 1 : 2) - (title2.isEmpty() ? 23 : 13);
	}
	
	@Override
	public boolean shouldRenderText() {
		return getTextHeight() + 10 < GuiBook.PAGE_HEIGHT;
	}
	
	protected abstract void drawRecipe(T recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second);
	protected abstract T loadRecipe(BookEntry entry, String loc);
	protected abstract ItemStack getRecipeOutput(T recipe);
	protected abstract int getRecipeHeight();
	
	protected int getX() {
		return GuiBook.PAGE_WIDTH / 2 - 49;
	}
	
	protected int getY() {
		return 4;
	}
	
	protected String getTitle(boolean second) {
		return second ? title2 : title1;
	}

}
