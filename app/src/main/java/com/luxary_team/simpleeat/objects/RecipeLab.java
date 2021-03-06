package com.luxary_team.simpleeat.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.luxary_team.simpleeat.database.RecipeCursorWrapper;
import com.luxary_team.simpleeat.database.RecipeDBHelper;
import com.luxary_team.simpleeat.database.RecipeDBShema.RecipeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class RecipeLab {

    private static RecipeLab sRecipeLab;
    private Context mContext;
    private SQLiteDatabase mDataBase;

    private RecipeLab(Context appContext) {
        mContext = appContext.getApplicationContext();
        mDataBase = new RecipeDBHelper(mContext).getWritableDatabase();
    }

    public static RecipeLab get(Context c){
        if (sRecipeLab == null) {
            sRecipeLab = new RecipeLab(c.getApplicationContext());
        }
        return sRecipeLab;
    }

    public static Recipe getNewRecipe(String recipeUUID) {
        return new Recipe(UUID.fromString(recipeUUID));
    }

    public void addRecipe(Recipe recipe) {
        ContentValues content = getContentValues(recipe);

        mDataBase.insert(RecipeTable.NAME, null, content);
    }

    public void deleteRecipe(Recipe recipe) {
        mDataBase.delete(RecipeTable.NAME, RecipeTable.Cols.UUID + " = ?", new String[]{recipe.getId().toString()});
    }

    public Recipe getRecipe(UUID id) {
        RecipeCursorWrapper cursor = queryRecipes(RecipeTable.Cols.UUID + " = ?", new String[]{id.toString()});

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecipe();
        } finally {
            cursor.close();
        }
    }

    public void updateRecipe(Recipe recipe) {
        String uuidString =recipe.getId().toString();
        ContentValues content = getContentValues(recipe);

        mDataBase.update(RecipeTable.NAME, content, RecipeTable.Cols.UUID + " = ?", new String[]{uuidString});

    }

    public ArrayList<Recipe> getRecipes() {
        ArrayList<Recipe> mRecipes = new ArrayList<>();
        RecipeCursorWrapper cursor = queryRecipes(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                mRecipes.add(cursor.getRecipe());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return mRecipes;
    }

    private RecipeCursorWrapper queryRecipes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(RecipeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new RecipeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues content = new ContentValues();
        content.put(RecipeTable.Cols.UUID, recipe.getId().toString());
        content.put(RecipeTable.Cols.TITLE, recipe.getTitle());
        content.put(RecipeTable.Cols.FAVORITE, recipe.isFavorite() ? 1 : 0);
        content.put(RecipeTable.Cols.TYPE, recipe.getRecipeType().toString());
        content.put(RecipeTable.Cols.TIME, recipe.getRecipeTime());
        content.put(RecipeTable.Cols.PORTIONS, recipe.getPortionCount());

        return content;
    }

    public File getPhotoFile(Recipe recipe) {
        File extfile = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (extfile == null)
            return null;

        return new File(extfile, recipe.getPhotoFileName());
    }

}
