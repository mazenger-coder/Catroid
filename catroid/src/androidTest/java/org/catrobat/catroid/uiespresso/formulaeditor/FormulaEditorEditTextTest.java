/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.formulaeditor;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Category.FUNCTIONS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Control.BACKSPACE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Control.COMPUTE;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Control.OK;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.FORMULA_EDITOR_TEXT_FIELD_MATCHER;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.onToast;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorEditTextTest {

	private String decimalMark;
	private String no;
	private String yes;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorEditTextTest");
		script.addBrick(new ChangeSizeByNBrick());
		baseActivityTestRule.launchActivity();
		no = getResourcesString(R.string.no);
		yes = getResourcesString(R.string.yes);
		decimalMark = getResourcesString(R.string.formula_editor_decimal_mark);
		onBrickAtPosition(1)
				.perform(click());
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testDoubleTapSelection() {
		onFormulaEditor()
				.performEnterFormula("1234");
		onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
				.perform(doubleClick());
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testLongClickDeletion() {
		onFormulaEditor()
				.performEnterFormula("1234");
		onView(BACKSPACE)
				.perform(longClick());
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFunctionDeletion() {
		String random = getResourcesString(R.string.formula_editor_function_rand) + "(0,1)";
		onFormulaEditor()
				.performOpenCategory(FUNCTIONS)
				.performSelect(random);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performOpenCategory(FUNCTIONS)
				.performSelect(random);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.checkShows(" ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testGoBackToDiscardChanges() {

		onFormulaEditor()
				.performEnterFormula("99.9+");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(no))
				.perform(click());

		onToast(withText(R.string.formula_editor_changes_discarded))
				.check(matches(isDisplayed()));
		onBrickAtPosition(1)
				.checkShowsText("0 ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testDiscardDialogDiscardSaveYes() {
		onFormulaEditor()
				.performEnterFormula("99");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		pressBack();

		onFormulaEditor()
				.performEnterFormula(".9");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(yes))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_saved))
				.check(matches(isDisplayed()));
		onBrickAtPosition(1)
				.checkShowsText("99" + decimalMark + "9 ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testDiscardDialogDiscardSaveNo() {
		onFormulaEditor()
				.performEnterFormula("99.9");
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(no))
				.perform(click());
		onToast(withText(R.string.formula_editor_changes_discarded))
				.check(matches(isDisplayed()));
		onBrickAtPosition(1)
				.checkShowsText("0 ");
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testFormulaIsNotValidToast() {
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(OK);
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));

		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performEnterFormula("+");
		onFormulaEditor()
				.performClickOn(OK);
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));

		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performEnterFormula("1+1+");
		pressBack();
		onView(withText(R.string.formula_editor_discard_changes_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(yes))
				.perform(click());
		onToast(withText(R.string.formula_editor_parse_fail))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void testComputeDialog() {
		onFormulaEditor()
				.performEnterFormula("-2");
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("-2.0")));
		pressBack();

		onFormulaEditor()
				.performEnterFormula("-6.111-");
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onFormulaEditor()
				.performClickOn(BACKSPACE);
		onFormulaEditor()
				.performClickOn(COMPUTE);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("-8.111")));
	}
}
