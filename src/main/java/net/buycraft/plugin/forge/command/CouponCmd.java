package net.buycraft.plugin.forge.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.buycraft.plugin.data.Coupon;
import net.buycraft.plugin.forge.BuycraftPlugin;
import net.buycraft.plugin.shared.util.CouponUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class CouponCmd {
    private static final int COUPON_PAGE_LIMIT = 10;
    private final BuycraftPlugin plugin;

    public CouponCmd(final BuycraftPlugin plugin) {
        this.plugin = plugin;
    }

    public int create(CommandContext<CommandSource> context) {
        final Coupon coupon;
        try {
            coupon = CouponUtil.parseArguments(StringArgumentType.getString(context, "data").split(" "));
        } catch (Exception e) {
            ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString(ForgeMessageUtil.format("coupon_creation_arg_parse_failure", e.getMessage()))
                    .setStyle(BuycraftPlugin.ERROR_STYLE));
            return 0;
        }

        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().createCoupon(coupon).execute();
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString(ForgeMessageUtil.format("coupon_creation_success", coupon.getCode()))
                        .setStyle(BuycraftPlugin.SUCCESS_STYLE));
            } catch (IOException e) {
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString(ForgeMessageUtil.format("generic_api_operation_error"))
                        .setStyle(BuycraftPlugin.ERROR_STYLE));
            }
        });

        return 1;
    }

    public int delete(CommandContext<CommandSource> context) {
        String code = StringArgumentType.getString(context, "code");
        plugin.getPlatform().executeAsync(() -> {
            try {
                plugin.getApiClient().deleteCoupon(code).execute();
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString(ForgeMessageUtil.format("coupon_deleted")).setStyle(BuycraftPlugin.SUCCESS_STYLE));
            } catch (Exception e) {
                ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString(e.getMessage()).setStyle(BuycraftPlugin.ERROR_STYLE));
            }
        });

//        ForgeMessageUtil.sendMessage(context.getSource(), new TextComponentString("pls3"));
        return 1;
    }
}
