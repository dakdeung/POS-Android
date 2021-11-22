package com.example.skripsi_kamal.config;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.example.skripsi_kamal.BaseApp;
import com.example.skripsi_kamal.BuildConfig;
import com.example.skripsi_kamal.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;

public class Utilities {
    /**
     * Internal antrian.
     */
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");

    /**
     * Antrian secara spesific.
     */
    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");


    /**
     * Antrian secara spesific.
     */
    public static volatile DispatchQueue cartQueue = new DispatchQueue("cartQueue");

    /**
     * Antrian secara seluruh image.
     */
    public static volatile DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");

    /**
     * Antrian secara seluruh image.
     */
    public static volatile DispatchQueue galleryLoadQueue = new DispatchQueue("galleryLoadQueue");

    /**
     * String txt = loadAssetTextAsString(this, "lokal.json");
     *
     * @param context Context yang digunakan
     * @param name    Nama File .txt yang akan diload
     * @return String
     */
    public static String loadAssetAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buf.append('\n');
                }
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return "";
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename Nama File .txt yang akan diload
     * @return String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int buffLen = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), buffLen);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(buffLen);
            byte[] bytes = new byte[buffLen];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), StandardCharsets.UTF_8) : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <V extends View> V findView(final View root, final int id) {
        V v = null;
        try {
            v = root.findViewById(id);
        } catch (final ClassCastException cce) {
            Timber.e(cce);
        }
        return v;
    }

    /**
     * Change currency price format
     *
     * @param amount value yang akan dirubah
     * @return String
     */
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("Rp ###,###,###");
        return formatter.format(Double.valueOf(amount));
    }

    public static String decimalFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(Double.valueOf(amount));
    }

    /**
     * Ambil nilai OS yang digunakan oleh customer.
     */
    public static String currentOsVersion() {
        double release = Double.parseDouble(Build.VERSION.RELEASE
                .replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
        String codeName = "Unsupported";
        if (release >= 4.1 && release < 4.4) {
            codeName = "Jelly Bean";
        } else if (release < 5) {
            codeName = "Kit Kat";
        } else if (release < 6) {
            codeName = "Lollipop";
        } else if (release < 7) {
            codeName = "Marshmallow";
        } else if (release < 8) {
            codeName = "Nougat";
        } else if (release < 9) {
            codeName = "Oreo";
        } else if (release < 10) {
            codeName = "Pie";
        } else if (release < 11) {
            codeName = "Q";
        }
        return codeName;
    }

    public static double fixLocationCoord(double value) {
        return ((long) (value * 1000000)) / 1000000.0;
    }

    /**
     * Email format validation
     */
    public static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
                    + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
                    + "("
                    + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
                    + ")+");

    /**
     * FAB hide show.
     *
     * @param v
     */
    public static void hideFirstFab(final View v) {
        v.setVisibility(View.GONE);
        v.setTranslationY(v.getHeight());
        v.setAlpha(0f);
    }

    /**
     * Get ApplicationName
     *
     * @param context Context
     * @return String
     */
    public static String getAppLable(Context context) {
        PackageManager packageManager = BaseApp.applicationContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager
                    .getApplicationInfo(BaseApp.applicationContext.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (applicationInfo != null
                ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

    /**
     * FAB animation.
     *
     * @param v      view.
     * @param rotate tipe rotasi.
     * @return rotasi.
     */
    public static boolean twistFab(final View v, boolean rotate) {
        v.animate().setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 180f : 0f);
        return rotate;
    }

    /**
     * FAB show.
     *
     * @param v view.
     */
    public static void showFab(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(300)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    /**
     * hide FAB.
     *
     * @param v view.
     */
    public static void hideFab(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setTranslationY(0);
        v.animate()
                .setDuration(300)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    /**
     * Tampilkan pesan error ke customer.
     *
     * @param view    View yang akan di-attach.
     * @param message pesan yang akan ditampilkan.
     */
    @SuppressLint("WrongConstant")
    public static void snackbarError(View view, String message) {
        snackbarError(view, message, SnackActionType.OK, null);
    }

    public static void snackBarErrorIndefinite(View view, String message) {
        snackbarErrorIndefinite(view, message);
    }

    /**
     * Tampilkan pesan error ke customer.
     *
     * @param view            View yang akan di-attach.
     * @param message         pesan yang akan ditampilkan.
     * @param snackActionType Tipe button yang akan ditampilkan.
     * @param actionCallback  callback yang diberikan saat button diclick.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    public static void snackbarError(View view, String message, SnackActionType snackActionType,
                                     SnackbarAction actionCallback) {
        String btnAction;
        int duration = 6000;
        if (snackActionType == SnackActionType.OK) {
            btnAction = "OK";
        } else {
            btnAction = "RETRY";
            duration = 5000;
        }
        final Snackbar sb = Snackbar.make(view,
                message, duration);
        sb.setActionTextColor(ContextCompat.getColor(BaseApp.applicationContext, R.color.white));
        sb.setAction(btnAction, view1 -> {
            if (snackActionType == SnackActionType.OK) {
                sb.dismiss();
            } else {
                actionCallback.retryAction(view1);
            }
        });
        sb.getView().setBackground(view.getContext().getDrawable(R.drawable.bg_snackbar_primary));
        sb.show();
    }

    /**
     * Tampilkan pesan error yang tidak hilang otomatis
     *
     * @param view
     * @param message
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void snackbarErrorIndefinite(View view, String message) {
        final Snackbar sb = Snackbar.make(view,
                message, LENGTH_INDEFINITE);
        sb.setActionTextColor(ContextCompat.getColor(BaseApp.applicationContext, R.color.white));
        sb.setAction("OK", view1 -> {
            sb.dismiss();
        });
        sb.getView().setBackground(view.getContext().getDrawable(R.drawable.bg_snackbar_primary));
        sb.show();
    }

    /**
     * Tampilkan pesan error ke customer.
     *
     * @param view    View yang akan di-attach.
     * @param message pesan yang akan ditampilkan.
     */
    @SuppressLint("WrongConstant")
    public static void toastError(View view, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View layout = layoutInflater.inflate(R.layout.toast_custom, view.findViewById(R.id.custom_toast_layout_id));
        TextView text = layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText(message);
        CardView lytCard = layout.findViewById(R.id.lyt_card);
        lytCard.setCardBackgroundColor(view.getContext().getResources().getColor(R.color.colorError));

        Toast toast = new Toast(view.getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @SuppressLint("WrongConstant")
    public static void toastErrorDebug(View view, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View layout = layoutInflater.inflate(R.layout.toast_custom, view.findViewById(R.id.custom_toast_layout_id));
        TextView text = layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText("NO_PRODUCTION : " + message);
        CardView lytCard = layout.findViewById(R.id.lyt_card);
        lytCard.setCardBackgroundColor(view.getContext().getResources().getColor(R.color.colorError));
        if (BuildConfig.DEBUG) {
            Toast toast = new Toast(view.getContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    /**
     * Tampilkan pesan warning ke customer.
     *
     * @param view    View yang akan di-attach.
     * @param message pesan yang akan ditampilkan.
     */
    @SuppressLint("WrongConstant")
    public static void toastWarning(View view, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View layout = layoutInflater.inflate(R.layout.toast_custom, view.findViewById(R.id.custom_toast_layout_id));
        TextView text = layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText(message);
        CardView lytCard = layout.findViewById(R.id.lyt_card);
        lytCard.setCardBackgroundColor(view.getContext().getResources().getColor(R.color.colorWarning));

        Toast toast = new Toast(view.getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Tampilkan pesan success ke customer.
     *
     * @param view    View yang akan di-attach.
     * @param message pesan yang akan ditampilkan.
     */
    @SuppressLint("WrongConstant")
    public static void toastSuccess(View view, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View layout = layoutInflater.inflate(R.layout.toast_custom, view.findViewById(R.id.custom_toast_layout_id));
        TextView text = layout.findViewById(R.id.text);
        text.setTextColor(Color.WHITE);
        text.setText(message);
        CardView lytCard = layout.findViewById(R.id.lyt_card);
        lytCard.setCardBackgroundColor(view.getContext().getResources().getColor(R.color.colorSuccess));

        Toast toast = new Toast(view.getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Tampilkan pesan error ke customer.
     *
     * @param view            View yang akan di-attach.
     * @param message         pesan yang akan ditampilkan.
     * @param snackActionType Tipe button yang akan ditampilkan.
     * @param actionCallback  callback yang diberikan saat button diclick.
     */
    @SuppressLint("WrongConstant")
    public static void snackbarErrorInfinite(View view, String message, SnackActionType snackActionType,
                                             SnackbarAction actionCallback) {
        String btnAction;
        if (snackActionType == SnackActionType.OK) {
            btnAction = "OK";
        } else {
            btnAction = "RETRY";
        }
        final Snackbar sb = Snackbar.make(view,
                message, LENGTH_INDEFINITE);
        sb.getView().setBackgroundColor(BaseApp.applicationContext.getResources()
                .getColor(android.R.color.holo_red_light));
        sb.setActionTextColor(ContextCompat.getColor(BaseApp.applicationContext, R.color.white));
        sb.setAction(btnAction, view1 -> {
            if (snackActionType == SnackActionType.OK) {
                sb.dismiss();
            } else {
                actionCallback.retryAction(view1);
            }
        });
        sb.show();
    }

    public interface SnackbarAction {
        void retryAction(View view);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void fadeOut(View view) {
        view.animate()
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

//    public static void accessDenied(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View view = inflater.inflate(R.layout.dialog_access_denied, null);
//        final BottomSheetDialog dialog =
//                new BottomSheetDialog(Objects.requireNonNull(context));
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//
//        ImageView ivClose = dialog.findViewById(R.id.iv_close);
//
//        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//
//        ivClose.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.setOnShowListener(dialog2 -> {
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                    ((View) view.getParent()).getLayoutParams();
//            View parent = (View) view.getParent();
//            parent.setFitsSystemWindows(true);
//            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
//            view.measure(0, 0);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            display.getMetrics(displaymetrics);
//            int screenHeight = displaymetrics.heightPixels;
//            bottomSheetBehavior.setPeekHeight(screenHeight);
//            params.height = screenHeight;
//            parent.setLayoutParams(params);
//        });
//        dialog.setCancelable(true);
//        dialog.show();
//    }
//
//    public static void accessDeniedOutlet(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View view = inflater.inflate(R.layout.dialog_access_denied, null);
//        final BottomSheetDialog dialog =
//                new BottomSheetDialog(Objects.requireNonNull(context));
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//
//        ImageView ivClose = dialog.findViewById(R.id.iv_close);
//        TextView tvMesage = dialog.findViewById(R.id.tv_message);
//
//        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//
//        tvMesage.setText("Anda tidak memiliki slot outlet yang tersedia\nharap subscribe terlebih dahulu");
//        ivClose.setOnClickListener(v -> {
//            dialog.dismiss();
//            ((Activity) context).finish();
//        });
//
//        dialog.setOnShowListener(dialog2 -> {
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                    ((View) view.getParent()).getLayoutParams();
//            View parent = (View) view.getParent();
//            parent.setFitsSystemWindows(true);
//            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
//            view.measure(0, 0);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            display.getMetrics(displaymetrics);
//            int screenHeight = displaymetrics.heightPixels;
//            bottomSheetBehavior.setPeekHeight(screenHeight);
//            params.height = screenHeight;
//            parent.setLayoutParams(params);
//        });
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    public static void accessDeniedPrintImage(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View view = inflater.inflate(R.layout.dialog_access_denied, null);
//        final BottomSheetDialog dialog =
//                new BottomSheetDialog(Objects.requireNonNull(context));
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//
//        ImageView ivClose = dialog.findViewById(R.id.iv_close);
//        TextView tvMesage = dialog.findViewById(R.id.tv_message);
//
//        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//
//        tvMesage.setText("Gambar terlalu besar, harap pilih gambar lagi");
//        ivClose.setOnClickListener(v -> {
//            dialog.dismiss();
//            //((Activity) context).finish();
//        });
//
//        dialog.setOnShowListener(dialog2 -> {
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                    ((View) view.getParent()).getLayoutParams();
//            View parent = (View) view.getParent();
//            parent.setFitsSystemWindows(true);
//            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
//            view.measure(0, 0);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            display.getMetrics(displaymetrics);
//            int screenHeight = displaymetrics.heightPixels;
//            bottomSheetBehavior.setPeekHeight(screenHeight);
//            params.height = screenHeight;
//            parent.setLayoutParams(params);
//        });
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    public static void accessDeniedPromo(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View view = inflater.inflate(R.layout.dialog_access_denied, null);
//        final BottomSheetDialog dialog =
//                new BottomSheetDialog(Objects.requireNonNull(context));
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//
//        ImageView ivClose = dialog.findViewById(R.id.iv_close);
//        TextView tvMesage = dialog.findViewById(R.id.tv_message);
//
//        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//
//        tvMesage.setText("Anda tidak dapat menambah lebih dari 2 promo");
//        ivClose.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.setOnShowListener(dialog2 -> {
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                    ((View) view.getParent()).getLayoutParams();
//            View parent = (View) view.getParent();
//            parent.setFitsSystemWindows(true);
//            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
//            view.measure(0, 0);
//            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            Display display = wm.getDefaultDisplay();
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            display.getMetrics(displaymetrics);
//            int screenHeight = displaymetrics.heightPixels;
//            bottomSheetBehavior.setPeekHeight(screenHeight);
//            params.height = screenHeight;
//            parent.setLayoutParams(params);
//        });
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    public static Boolean minMax(double min, double max, double input, String inputValue) {
//        if (inputValue.contains(".") && inputValue.split("\\.").length > 1) {
//            return (max > min ? input >= min && input <= max : input >= max && input <= min)
//                    && (inputValue.split("\\.")[1].length() < 3);
//        } else {
//            return (max > min ? input >= min && input <= max : input >= max && input <= min);
//        }
//    }
//
//    public static String leftRightAlign(String str1, String str2) {
//        int length = str1.length() + str2.length();
//        String ans = str1 + " " + str2;
//        if (PrinterConfig.getInstance().getPrinterSize() == 32) {
//            if (length <= PrinterConfig.getInstance().getPrinterSize()) {
//                int n = (32 - length);
//                ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
//            }
//        } else {
//            if (length <= PrinterConfig.getInstance().getPrinterSize()) {
//                int n = (48 - length);
//                ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
//            }
//        }
//        return ans;
//    }
//
//    // To animate view slide out from right to left
//    public static void slideToLeft(View view, Context context) {
//        view.setVisibility(View.VISIBLE);
//        view.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.slide_in_left_animation));
//    }
//
//    // To animate view slide out from left to right
//    public static void slideToRight(View view, Context context) {
//        view.setVisibility(View.GONE);
//        view.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.slide_out_right_animation));
//    }
}
