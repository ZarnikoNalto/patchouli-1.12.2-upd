package vazkii.patchouli.common.book;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.common.base.Patchouli;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books";

	public final Map<ResourceLocation, Book> books = new HashMap<>();
	public Gson gson;

	private BookRegistry() { 
		gson = new GsonBuilder().create();
	}

	public void init() {
		List<ModContainer> mods = Loader.instance().getActiveModList();
		Map<Pair<ModContainer, ResourceLocation>, String> foundBooks = new HashMap<>();

		mods.forEach((mod) -> {
			String id = mod.getModId();
			CraftingHelper.findFiles(mod, String.format("assets/%s/%s", id, BOOKS_LOCATION), (path) -> Files.exists(path),
					(path, file) -> {
						if(file.toString().endsWith("book.json")) {
							String fileStr = file.toString().replaceAll("\\\\", "/");
							String relPath = fileStr.substring(fileStr.indexOf(BOOKS_LOCATION) + BOOKS_LOCATION.length() + 1);
							String bookName = relPath.substring(0, relPath.indexOf("/"));

							if(bookName.contains("/")) {
								(new IllegalArgumentException("Ignored book.json @ " + file)).printStackTrace();
								return true;
							}

							String assetPath = fileStr.substring(fileStr.indexOf("/assets"));
							ResourceLocation bookId = new ResourceLocation(id, bookName);
							foundBooks.put(Pair.of(mod, bookId), assetPath);
						}

						return true;
					}, false, true);
		});

		foundBooks.forEach((pair, file) -> {
			ModContainer mod = pair.getLeft();
			ResourceLocation res = pair.getRight();

			InputStream stream = mod.getMod().getClass().getResourceAsStream(file);
			loadBook(mod, res, stream, false);
		});
		
		BookFolderLoader.findBooks();


		System.out.println("_________________________");
		System.out.println("BOOKS DICTIONARY!!! BOOKS DICTIONARY!!! BOOKS DICTIONARY!!!");
		System.out.println(books);
	}
	
	public void loadBook(ModContainer mod, ResourceLocation res, InputStream stream, boolean external) {
		Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		Book book = gson.fromJson(reader, Book.class);

		books.put(res, book);
		book.build(mod, res, external);
	}

	@SideOnly(Side.CLIENT)
	public void reload() {
		books.values().forEach(Book::reloadContents);
		books.values().forEach(Book::reloadExtensionContents);
		ClientAdvancements.updateLockStatus(false);
	}

}
