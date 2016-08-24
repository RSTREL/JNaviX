package org.oakimsoft.common;

public class JNavixActions {
	
	/**
	 * UI_Update   - прямые указания интерфейсу (Обновить что-либо)
	 * UI_Call     - События от кнопок 
	 * APP_        - Общие события приложения (Переключения слоев)
	 */
	
	
	
	
	/**
	 * Обновить параметры на информационной панели. Прочесть из активного слоя
	 */
	static public String UI_UpdateInfoPanel = "UI.Update.InfoPanel"; 
	 /**
	 * Обновить изображение схемы на экране, при этом полной перерисовки не наступает
	 */
	static public String UI_UpdateMapPicture = "UI.Update.MapPicture"; 
	/**
	 * Событие от кнопки на изменение азимута  
	 */

	static public String UI_UpdateToolbar = "UI.UpdateToolbar"; 


	/**
	 *  Добавить слой 
	 */
	static public String APP_LayerAdd = "App.Layer.Add"; 

	/**
	 *  Добавить слой КАРТА 
	 */
	static public String APP_LayerAddMap = "App.Layer.Add.Map"; 	
	
	/**
	 *  Добавить слой Рисунок
	 */
	static public String APP_LayerAddSketch = "App.Layer.Add.Sketch"; 		
	/**
	 *  Добавить слой GPS Track
	 */
	static public String APP_LayerAddGPSTrack = "App.Layer.Add.GPSTrack";
	/**
	 * Произошла фокусировка на другой слой Команда|ПорядковыйНомерСлоя
	 */
	static public String APP_LayerFocused = "App.Layer.Focused";

	/**
	 * Переместить активный слой на уровень вверх
	 */
	static public String APP_LayerUpFocused = "App.Layer.UpFocused";
	/**
	 * Переместить активный слой на уровень вверх
	 */
	static public String APP_LayerDnFocused = "App.Layer.DnFocused";
	/**
	 * Удалить активный слой 
	 */
	static public String APP_LayerRemoveFocused = "App.Layer.RemoveFocused";

	
	
	/**
	 * Обновить основное изображение схемы. Запустить оторисовку всех видимых слоев 
	 */	
	static public String APP_UpdateMapPicture = "App.Update.MapPicture"; 	
	
	
	/**
	 * Событие от кнопки на изменение масштаба  
	 */
	static public String UI_CallScaleChange = "UI.Call.ScaleChange"; 
	
	/**
	 * Событие от кнопки на изменение азимута  
	 */
	static public String UI_CallAzimuthChange = "UI.Call.AzimuthChange"; 

	/**
	 * Событие от кнопки на изменение азимута  
	 */
	static public String UI_CallDDCenterChange = "UI.Call.DDCenterChange"; 
	/**
	 * Щелчок по ячейке в списке слоев. Формат: Команда|Колонка|Строка
	 */
	static public String UI_CallCellClicked = "UI.Call.CellClicked";
	

	
	
	/**
	 * DrawBoard. Кнопка выбора цвета рисования
	 */
	static public final String UI_DrawBoardBtnColor = "UI.DrawBoardBtnColor";	
	/**
	 * DrawBoard. Кнопка сохранения рисования
	 */	
	static public final String UI_DrawBoardBtnSave = "UI.DrawBoardBtnSave";
	/**
	 * DrawBoard. Кнопка открытия рисования
	 */	
	static public final String UI_DrawBoardBtnOpen = "UI.DrawBoardBtnOpen";
	/**
	 * DrawBoard. Кнопка очистки
	 */	
	static public final String UI_DrawBoardBtnClear = "UI.DrawBoardBtnClear";
	/**
	 * DrawBoard. Кнопка Свойств
	 */	
	static public final String UI_DrawBoardBtnProperties = "UI.DrawBoardBtnProperties";
	
	
	
	
	/**
	 * Unimap. Кнопка ОТКРЫТЬ
	 */
	static public final String UI_UnimapBtnOpen = "UI.UnimapBtnOpen";		
	/**
	 * Unimap. Кнопка Свойства
	 */	
	static public final String UI_UnimapBtnProperties = "UI.UnimapBtnProperties";
	

	
	/**
	 * GPSTrack. Кнопка ОТКРЫТЬ
	 */
	static public final String UI_GPSTrackBtnOpen = "UI.GPSTrackBtnOpen";		
	/**
	 * GPSTrack. Кнопка Свойства
	 */	
	static public final String UI_GPSTrackBtnProperties = "UI.GPSTrackBtnProperties";	
	
	
	/**
	 * Главный тулбар. Кнопка ОТКРЫТЬ
	 */
	static public final String UI_MainBtnOpen = "UI.MainBtnOpen";		
	/**
	 * GPSTrack. Кнопка Автоцентрирование
	 */	
	static public final String UI_MainBtnAutocenter = "UI.MainBtnAutocenter";
	
	
	/**
	 * UI Обновить список слоев
	 */	
	static public final String UI_UpdateLayersList  = "UI.UpdateLayersLists";	

	
	
}
