# Bloqqi Editor - Release Notes

This file contains the important changes for each release. For a comprehension list of changes, see the commit log.

## 0.1.14 - 2016-05-04

- Feature: Add Eclipse Preference "Enable experimental features"
- Feature: Specialization wizard allows the user to expose inner parameters as outer parameters on the newly created component/diagram type
- Feature: Component specialization can now be changed after creation. However, manually added functionality to the component will be lost!
- Experimental Feature: Subtypes can be extracted as recommendations (right click on a diagram).
- Fix: Connections were lost when changing parameters/diagram properties

## 0.1.13 - 2016-03-16

- Changed plugin name from PicoDiagram Editor (org.picodiagram.editor) to Bloqqi Editor (org.bloqqi.editor)
- Fix auto layout when literals are used
- Fix position for newly created blocks when zoomed in
- Fix showing connections between two blocks belonging to two different inlined components
- Fix removing anonymous diagram types

## 0.1.12 - 2016-03-09

- Feature: Show literal values
- Feature: Editing operations for adding and removing literal values
- Fix bug when removed diagram type

## 0.1.11 - 2016-03-01

- Feature: Add delete diagram type edit operation
- Feature: Add Open Declaration to context menu for components
- Feature: Show in Outline View which diagram that is currently displayed
- Feature: String type and String literals are supported by the compiler
- Feature: Allow boolean literals true and false in diagram types
- Fix: Improve handling of connections when a component is removed or when supertypes are changed
- Fix graphical bug in specialization wizard on Windows

## 0.1.10 - 2016-02-02

- Fix: Changed so that when a block is declared as *replaceable* in a recommendation, then all named subtypes are shown in the wizard (and not only the compatible subtypes).

## 0.1.9 - 2016-01-29

- Feature: Added Eclipse Preference page for the editor
- Feature: Always ask for a name when components are created
  - This feature can be disabled in the Preferences
- Fixed bug in auto layout when inlining a block that contains errors
