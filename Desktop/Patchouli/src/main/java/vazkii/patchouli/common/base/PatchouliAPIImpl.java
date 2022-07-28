package vazkii.patchouli.common.base;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.multiblock.Multiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;
import vazkii.patchouli.common.util.ItemStackUtil;

public class PatchouliAPIImpl implements IPatchouliAPI {
	
	public static final PatchouliAPIImpl INSTANCE = new PatchouliAPIImpl();
	
	private PatchouliAPIImpl() {
	}
	
	@Override
	public boolean isStub() {
		return false;
	}
	
	@Override
	public void setConfigFlag(String flag, boolean value) {
		PatchouliConfig.setFlag(flag, value);
	}
	
	@Override
	public boolean getConfigFlag(String flag) {
		return PatchouliConfig.getConfigFlag(flag);
	}
	
	@Override
	public void openBookGUI(EntityPlayerMP player, ResourceLocation book) {
		NetworkHandler.INSTANCE.sendTo(new MessageOpenBookGui(book.toString()), player);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void openBookGUI(ResourceLocation book) {
		ClientBookRegistry.INSTANCE.displayBookGui(book.toString());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getOpenBookGui() {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui instanceof GuiBook)
			return ((GuiBook) gui).book.resourceLoc;
		return null;
	}
	
	@Override
	public void reloadBookContents() {
		Patchouli.proxy.requestBookReload();
	}
	
	@Override
	public ItemStack getBookStack(String book) {
		return ItemModBook.forBook(book);
	}
	
	@Override
	public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider) {
		InputStream testStream = streamProvider.get();
		if (testStream == null)
			throw new NullPointerException("Stream provider can't return a null stream");
		
		if (BookContents.addonTemplates.containsKey(res))
			throw new IllegalArgumentException("Template " + res + " is already registered");
		
		BookContents.addonTemplates.put(res, () -> {
			InputStream stream = streamProvider.get();
			InputStreamReader reader = new InputStreamReader(stream);
			return ClientBookRegistry.INSTANCE.gson.fromJson(reader, BookTemplate.class);
		});
	}
	
	@Override
	public ItemStack deserializeItemStack(String str) {
		return ItemStackUtil.loadStackFromString(str);
	}
	
	@Override
	public String serializeItemStack(ItemStack stack) {
		return ItemStackUtil.serializeStack(stack);
	}
	
	@Override
	public Ingredient deserializeIngredient(String str) {
		return ItemStackUtil.loadIngredientFromString(str);
	}
	
	@Override
	public String serializeIngredient(Ingredient ingredient) {
		return ItemStackUtil.serializeIngredient(ingredient);
	}
	
	@Override
	public IMultiblock getMultiblock(ResourceLocation res) {
		return MultiblockRegistry.MULTIBLOCKS.get(res);
	}
	
	@Override
	public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb) {
		return MultiblockRegistry.registerMultiblock(res, mb);
	}
	
	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return new Multiblock(pattern, targets);
	}
	
	@Override
	public IStateMatcher predicateMatcher(IBlockState display, Predicate<IBlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}
	
	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<IBlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}
	
	@Override
	public IStateMatcher stateMatcher(IBlockState state) {
		return StateMatcher.fromState(state);
	}
	
	@Override
	public IStateMatcher looseBlockMatcher(Block block) {
		return StateMatcher.fromBlockLoose(block);
	}
	
	@Override
	public IStateMatcher strictBlockMatcher(Block block) {
		return StateMatcher.fromBlockStrict(block);
	}
	
	@Override
	public IStateMatcher displayOnlyMatcher(IBlockState state) {
		return StateMatcher.displayOnly(state);
	}
	
	@Override
	public IStateMatcher displayOnlyMatcher(Block block) {
		return StateMatcher.displayOnly(block);
	}
	
	@Override
	public IStateMatcher airMatcher() {
		return StateMatcher.AIR;
	}
	
	@Override
	public IStateMatcher anyMatcher() {
		return StateMatcher.ANY;
	}
	
}
