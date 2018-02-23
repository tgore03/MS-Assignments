574 Assignment 2

Steps to reproduce experiment:

Q1 a)
	Preprocess Tab -> Open file House-votes-84
	Classify Tab -> Classifier -> choose -> trees -> j48
	check Use Training set -> select (Nom) class -> Start
	Result Set -> right click on trees_J48 -> visualize tree

Q1 b)
	Repeat the above process for Breast-Cancer-Wisconcin dataset

Q2)
	Open Weka -> Knowledge Flow
	DataSource -> ArffLoader -> place on canvas -> right click -> configure -> select house-votes-84 dataset
	Evaluation -> ClassAssigner -> place -> right click on ArffLoader -> connect dataset to this node (Method to connect two nodes)
	Evaluation -> CrossValidationFoldMaker -> place -> right click -> configure -> number of folds = 5 -> connect ClassAssigner on dataset
	Classifier -> trees -> J48 -> place -> Connect CrossValidationFoldMaker to this node on both trainingset and testset
	Visualization -> GraphViewer -> connect J48 to this node on graph
	Evaluation -> ClassifierPerformanceEvaluator -> place -> Connect J48 to this node on batchClassifier
	Visualization -> TextViewer -> Place -> Connect ClassifierPerformanceEvaluator to this node on -> text
	
	Right click GraphViewer -> show plots
	Right click TextViewer -> show results
	
	
Q3) 
	Preprocess Tab -> Open file Breast-Cancer-Wisconcin
	Classify Tab -> Classifier -> choose -> trees -> j48
	Check Cross-validation -> Folds = 5 -> select (Nom) class -> Start
	Use incorrectly classified (r) and total testing instances (n) to obtain errors(r) = r/n
	calculate Std Dev. using sig =sqrt(errors(r)*(1-errors(r))/ n)
	calculate confidence interval as [errors(r) - z*sig, errors(r) + z*sig] where z = 1.96 (for 95%)
	
Q4 a)
	Preprocess Tab -> Open file House-votes-84
	Filter -> filters -> unsupervised -> attribute -> ReplaceMissingValues
	Classify Tab -> Classifier -> choose -> trees -> j48
	Check Cross-validation -> Folds = 5 -> select (Nom) class -> Start
	Result Set -> right click on trees_J48 -> visualize tree
	
Q4 b)
	Repeat the above process for Breast-Cancer-Wisconcin dataset
	

	