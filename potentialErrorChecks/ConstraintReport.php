<?php

namespace WikibaseQuality\ConstraintReport\Maintenance;

// @codeCoverageIgnoreStart
use Wikibase\DataModel\Entity\ItemId;
use Wikibase\Repo\WikibaseRepo;
use WikibaseQuality\ConstraintReport\EvaluateConstraintReportJobService;


$basePath = getenv( "MW_INSTALL_PATH" ) !== false ? getenv( "MW_INSTALL_PATH" ) : __DIR__ . "/../../..";
require_once $basePath . "/maintenance/Maintenance.php";
// @codeCoverageIgnoreEnd

class ConstraintReport extends \Maintenance {

	public function __construct() {
		parent::__construct();
		$this->mDescription = "Checks constraints on items. Add --start and/or --end when you only want to check some items";
		$this->addOption( 'start', 'numeric item id the checks starts with', true, true );
		$this->addOption( 'end', 'numeric item id the checks ends with', true, true );
	}

	public function execute(){
	    $item = $this->getOption( 'start' ) ? $this->getOption( 'start' ) : 1; 
	    $end = $this->getOption( 'end' ) ? $this->getOption( 'end' ) : 21000000; 
		
		while( $item <= $end {
			$itemId = 'Q' . $item;
			echo "$itemId\n";
			$item += 1;
			$entity = $lookup->getEntity( new ItemId( $itemId ) );
			if ( $entity ) {
				$service = new EvaluateConstraintReportJobService();
				$params = array( 'entityId' => $itemId, 'referenceTimestamp' => null );
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