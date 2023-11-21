package com.raffa064.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import apk64.FileUtils;
import com.raffa064.engine.EditorActivity;
import com.raffa064.engine.core.json.JSONUtils;
import com.raffa064.engine.ui.ViewUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.raffa064.engine.modules.EngineDataModule;
import com.raffa064.engine.environments.editor.EditorCore;
import static com.raffa064.engine.environments.editor.EditorCore.*;

public class HomeActivity extends Activity {
	private EditorCore core;
	private EngineDataModule engineDataModule;
	
	private File engineDir;
	private ListView projectList;
	private Button createProject;
	private Button importProject;
	private LinearLayout createProjectDialogContainer;

	public HomeActivity() {
		core = EditorCore.instance();
		engineDataModule = new EngineDataModule(this);
		core.add(engineDataModule);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();
		setContentView(R.layout.activity_home);

		engineDir = new File(Environment.getExternalStorageDirectory(), "SquareEngine");
		
		File engineDataDir = getFilesDir();
		core.event(EVENT_CHANGE_ENGINE_DATA_DIR, engineDataDir);
		
		initializeViews();
		setupViews();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		onCreate(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		core.remove(engineDataModule);
	}

	public List<ProjectItem> loadProjectList() {
		List<ProjectItem> projects = new ArrayList<>();
		
		File lastProjectDir = (File) core.get(GET_LAST_OPENNED_PROJECT);
		if (lastProjectDir != null) {
			ProjectItem projectItem = new ProjectItem(lastProjectDir, true);
			projects.add(projectItem);
		}
		
		for (File dir : engineDir.listFiles()) {
			ProjectItem projectItem = new ProjectItem(dir);
			projects.add(projectItem);
		}

		return projects;
	}

	private void initializeViews() {
		projectList = findViewById(R.id.home_project_list);
		createProject = findViewById(R.id.home_create_project_button);
		importProject = findViewById(R.id.home_import_project_button);
		createProjectDialogContainer = findViewById(R.id.home_create_project_dialog_container);
	}

	private void setupViews() {
		List<ProjectItem> projects = loadProjectList();
		projectList.setAdapter(new ProjectListAdapter(this, projects));
		projectList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> ad, View view, int pos, long id) {
					ProjectItem item = (ProjectItem) ad.getItemAtPosition(pos);

					String name = item.getName();
					openProject(name);					
				}
			});

		createProject.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					initializeCreateProjectDialog();
				}
			});


		createProjectDialogContainer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					ViewUtils.gone(createProjectDialogContainer);
				}
			});
	}

	public void openProject(String name) {
		ViewUtils.gone(createProjectDialogContainer);
		
		Intent intent = new Intent(HomeActivity.this, EditorActivity.class);
		intent.putExtra(EditorActivity.EXTRA_PROJECT_DIR_NAME, name);
		startActivity(intent);
	}

	private void initializeCreateProjectDialog() {
		ViewUtils.visible(createProjectDialogContainer);

		// Initializing views
		final EditText createProjectName = createProjectDialogContainer.findViewById(R.id.create_project_dialog_name);
		final EditText createProjectPackage = createProjectDialogContainer.findViewById(R.id.create_project_dialog_package);
		final EditText createProjectWidth = createProjectDialogContainer.findViewById(R.id.create_project_dialog_width);
		final EditText createProjectHeight = createProjectDialogContainer.findViewById(R.id.create_project_dialog_height);
		final Switch createProjectKeepWidth = createProjectDialogContainer.findViewById(R.id.create_project_dialog_keep_width);
		final Switch createProjectDefaultStructure = createProjectDialogContainer.findViewById(R.id.create_project_dialog_default_structure);
		final Button createProjectApply = createProjectDialogContainer.findViewById(R.id.create_project_dialog_apply);

		// Setting default values
		createProjectName.setText("");
		createProjectPackage.setText("");
		createProjectWidth.setText("1280");
		createProjectHeight.setText("720");
		createProjectKeepWidth.setChecked(true);
		createProjectDefaultStructure.setChecked(true);

		createProjectApply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isEmptyInput(createProjectName)) return;
					if (isEmptyInput(createProjectPackage)) return;
					if (isEmptyInput(createProjectWidth)) return;
					if (isNaNInput(createProjectWidth)) return;
					if (isEmptyInput(createProjectHeight)) return;
					if (isNaNInput(createProjectHeight)) return;

					String name = createProjectName.getText().toString().trim();
					String packageName = createProjectPackage.getText().toString().trim();
					int width = Integer.parseInt(createProjectWidth.getText().toString().trim());
					int height = Integer.parseInt(createProjectHeight.getText().toString().trim());
					boolean keepWidth = createProjectKeepWidth.isChecked();
					boolean useDefaultStructure = createProjectDefaultStructure.isChecked();

					File projectDir = new File(engineDir, name);

					if (projectDir.exists()) {
						createProjectName.requestFocus();
						createProjectName.setError("Project already exists");
						return;
					}

					projectDir.mkdir();

					try {
						File configFile = new File(projectDir, "config.cfg");
						configFile.createNewFile();
						writeConfigs(name, packageName, width, height, keepWidth, configFile);
					} catch (Exception e) {
						Toast.makeText(HomeActivity.this, "Error on write configs: " + e, Toast.LENGTH_LONG).show();
					}

					if (useDefaultStructure) {
						try {
							File assetsDir = new File(projectDir, "assets");
							File scenesDir = new File(projectDir, "scenes");
							File scriptsDir = new File(projectDir, "scripts");

							assetsDir.mkdir();
							scenesDir.mkdir();
							scriptsDir.mkdir();

							File mainLoaderFile = new File(scriptsDir, "MainLoader.js");
							mainLoaderFile.createNewFile();
							String mainLoaderCode = readAsset("MainLoader.js");
							FileUtils.writeFile(mainLoaderFile, mainLoaderCode);

							File mainSceneFile = new File(scenesDir, "main.scn");
							mainLoaderFile.createNewFile();
							String mainSceneJson = readAsset("main-full.scn");
							FileUtils.writeFile(mainSceneFile, mainSceneJson);

							openProject(name);
						} catch (Exception e) {
							Toast.makeText(HomeActivity.this, "Error on create project files: " + e, Toast.LENGTH_LONG).show();
						}
					} else {
						try {
							File mainSceneFile = new File(projectDir, "main.scn");
							mainSceneFile.createNewFile();
							String mainSceneJson = readAsset("main-empty.scn");
							FileUtils.writeFile(mainSceneFile, mainSceneJson);
							
							openProject(name);
						} catch (Exception e) {
							Toast.makeText(HomeActivity.this, "Error on create main.scn: " + e, Toast.LENGTH_LONG).show();
						}
					}
				}

				private String readAsset(String fileName) throws IOException {
					InputStream open = getAssets().open(fileName);
					byte[] buffer = new byte[open.available()];
					open.read(buffer);
					String fileContent = new String(buffer);
					
					return fileContent;
				}

				private void writeConfigs(String name, String packageName, int width, int height, boolean keepWidth, File configFile) throws Exception {
					JSONObject json = new JSONObject();
					json.put("name", name);
					json.put("package", packageName);
					json.put("versionName", "1.0");
					json.put("versionCode", 1);
					json.put("mainScene", "main");
					json.put("defaultOrientation", "sensor");

					JSONObject viewportJson = new JSONObject();
					viewportJson.put("width", width);
					viewportJson.put("height", height);
					viewportJson.put("keepWidth", keepWidth);

					json.put("viewport", viewportJson);

					String jsonString = json.toString();

					FileUtils.writeFile(configFile, jsonString);
				}

				private boolean isEmptyInput(EditText editText) {
					if (editText.getText().toString().trim().isEmpty()) {
						editText.requestFocus();
						editText.setError("Can't be empty");
						return true;
					}

					return false;
				}

				private boolean isNaNInput(EditText editText) {
					try {
						Integer.parseInt(editText.getText().toString());
						return false;
					} catch (NumberFormatException e) {
						editText.requestFocus();
						editText.setError("Invalid number");
						return true;
					}
				}
			});
	}

	public static class ProjectItem {
		private File directory;
		private boolean lastOpenned;

		public ProjectItem(File directory, boolean lastOpenned) {
			this.directory = directory;
			this.lastOpenned = lastOpenned;
		}

		public ProjectItem(File directory) {
			this.directory = directory;
		}

		public void setLastOpenned(boolean lastOpenned) {
			this.lastOpenned = lastOpenned;
		}

		public boolean isLastOpenned() {
			return lastOpenned;
		}

		public Bitmap getIcon() {
			File configFile = new File(directory, "config.cfg");

			try {
				String configJSON = FileUtils.readFileString(configFile);

				JSONObject json = new JSONObject(configJSON);
				String icon = JSONUtils.getString(json, "icon", null);

				if (icon != null) {
					File iconFile = new File(directory, icon);

					if (iconFile.exists()) {
						Bitmap bitmap = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
						return bitmap;
					}
				}
			} catch (Exception e) {}

			return null;
		}

		public String getName() {
			return directory.getName();
		}
	}

	public static class ProjectListAdapter extends ArrayAdapter<ProjectItem> {
		public Context ctx;

		public ProjectListAdapter(Context ctx, List<ProjectItem> projects) {
			super(ctx, 0, projects);
			this.ctx = ctx;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProjectItem item = getItem(position);

			int layoutResId = item.isLastOpenned()? R.layout.home_project_item_last_openned : R.layout.home_project_item;
			View view = LayoutInflater.from(ctx).inflate(layoutResId, parent, false);

			ImageView icon = view.findViewById(R.id.project_item_icon);
			Bitmap iconBitmap = item.getIcon();

			if (iconBitmap != null) {
				icon.setImageBitmap(iconBitmap);	
			} else {
				icon.setImageResource(R.drawable.no_icon_project);
			}

			TextView name = view.findViewById(R.id.project_item_name);
			name.setText(item.getName());

			return view;
		}
	}
}
