ALTER TABLE `SectionGroupCosts` ADD COLUMN `Title` varchar(100) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `SubjectCode` varchar(4) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `CourseNumber` varchar(7) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `EffectiveTermCode` varchar(6) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `SequencePattern` varchar(3) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `TermCode` varchar(6) NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `UnitsLow` FLOAT NOT NULL;
ALTER TABLE `SectionGroupCosts` ADD COLUMN `UnitsHigh` FLOAT NOT NULL;
