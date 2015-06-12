<?php

namespace WikibaseQuality\ConstraintReport\Maintenance;

// @codeCoverageIgnoreStart
use Wikibase\DataModel\Entity\PropertyId;
use Wikibase\Repo\WikibaseRepo;
use WikibaseQuality\ConstraintReport\EvaluateConstraintReportJobService;


$basePath = getenv( "MW_INSTALL_PATH" ) !== false ? getenv( "MW_INSTALL_PATH" ) : __DIR__ . "/../../..";
require_once $basePath . "/maintenance/Maintenance.php";
// @codeCoverageIgnoreEnd

class ConstraintReport extends \Maintenance {

	public function __construct() {
		parent::__construct();
		$this->mDescription = "Checks constraints on all properties";
	}

	public function execute(){
	    $property = 1; 
	    $end = 2100; 
		
		while( $property <= $end {
			$propertyId = 'Q' . $property;
			echo "$propertyId\n";
			$property += 1;
			$entity = $lookup->getEntity( new PropertyId( $propertyId ) );
			if ( $entity ) {
				$service = new EvaluateConstraintReportJobService();
				$params = array( 'entityId' => $propertyId, 'referenceTimestamp' => null );
				$resultSummary = $service->getResults( $params );
				$messageToLog = $service->buildMessageForLog( $resultSummary, null, $params );
				$service->writeToLog( $messageToLog );
			}
		}
	}
}

// @codeCoverageIgnoreStart
$maintClass = 'WikibaseQuality\ConstraintReport\Maintenance\ConstraintReport';
require_once RUN_MAINTENANCE_IF_MAIN;