/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.plugin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import com.lidroid.plugin.mop.MopAgent;
import com.lidroid.plugin.mop.TargetType;

/**
 * Created with IntelliJ IDEA.
 * User: wyouflf
 * Date: 13-6-17
 * Time: PM 2:21
 * <p/>
 * the container of all modules
 */
public abstract class Container<T extends Activity> extends Plugin implements ModuleCircleLifeEvent {

    private InstallationReceiver installationReceiver = new InstallationReceiver();

    public Container(T mainActivity) {
        super(mainActivity);
        PluginManager.init(this);
    }

    @SuppressWarnings("unchecked")
    public T getMainActivity() {
        return (T) this.context;
    }

    /**
     * on start work，
     * please call it when mainActivity onStart.
     */
    @MopAgent(targetType = TargetType.AllModules, ignoreMopImpl = true)
    public final void onStart() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        this.context.registerReceiver(installationReceiver, filter);

        mopAction();
    }

    /**
     * on resume work，
     * please call it when mainActivity onResume.
     */
    @MopAgent(targetType = TargetType.AllModules, ignoreMopImpl = true)
    public final void onResume() {
        mopAction();

        PluginManager.getInstance().reLoadPlugin();
    }

    /**
     * on stop work，
     * please call it when mainActivity onStop.
     */
    @MopAgent(targetType = TargetType.AllModules, ignoreMopImpl = true)
    public final void onStop() {
        mopAction();

        this.context.unregisterReceiver(installationReceiver);
    }

    /**
     * verify package，
     * if return false, it will not load the package.
     *
     * @param packageName
     * @return
     */
    public abstract boolean verifyPackage(String packageName);
}
